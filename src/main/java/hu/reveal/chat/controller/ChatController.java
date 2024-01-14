package hu.nye.chat.controller;

import hu.nye.chat.enums.Topic;
import hu.nye.chat.model.Message;
import hu.nye.chat.model.Room;
import hu.nye.chat.model.User;
import hu.nye.chat.service.impl.ChatServiceImpl;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatController {

    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

    private final ChatServiceImpl chatService;

    @Autowired
    public ChatController(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/register")
    public void registerUser(@Payload final User newUser, final SimpMessageHeaderAccessor headerAccessor) {

        headerAccessor.getSessionAttributes().put("userId", newUser.getId());
        LOG.info("New user request: {}", newUser);
        List<User> possiblePartners;
        if (newUser.getTopics().isEmpty()) {
            possiblePartners = chatService.getPossiblePartnersWithNoTopic(newUser);
        } else {
            possiblePartners = chatService.getPossiblePartnersWithTopic(newUser);
        }

        if (possiblePartners.isEmpty()) {
            chatService.addPendingUser(newUser);
            return;
        }

        Pair<User, Topic> chatPartner = chatService.getMostAppropriatePartnerWithTopic(newUser, possiblePartners);
        User userTwo = chatPartner.getKey();
        Topic commonTopic = chatPartner.getValue();
        Room room = chatService.createRoom(List.of(userTwo, newUser), commonTopic);
        chatService.addRoom(room);
        chatService.removePendingUserById(userTwo.getId());
        chatService.sendConnectMessage(userTwo, newUser.getId(), commonTopic);
        chatService.sendConnectMessage(newUser, userTwo.getId(), commonTopic);
    }

    @MessageMapping("/message")
    public void sendMessage(@Payload final Message message) {
        LOG.info("New message request: {}", message);
        chatService.sendMessage(message);
    }
}
