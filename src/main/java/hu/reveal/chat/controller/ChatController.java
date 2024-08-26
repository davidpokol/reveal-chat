package hu.reveal.chat.controller;

import hu.reveal.chat.enums.Topic;
import hu.reveal.chat.model.Message;
import hu.reveal.chat.model.Room;
import hu.reveal.chat.model.User;
import hu.reveal.chat.service.impl.ChatServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("userId", newUser.id());
        LOG.info("New user request: {}", newUser);
        ArrayList<User> possiblePartners;
        if (newUser.topics().isEmpty()) {
            possiblePartners = chatService.getPossiblePartnersWithNoTopic(newUser);
        } else {
            possiblePartners = chatService.getPossiblePartnersWithTopic(newUser);
        }

        if (possiblePartners.isEmpty()) {
            chatService.addPendingUser(newUser);
            return;
        }

        Map<User, Topic> chatPartner = chatService.getMostAppropriatePartnerWithTopic(newUser, possiblePartners);
        User userTwo = chatPartner.keySet().iterator().next();
        Topic commonTopic = chatPartner.values().iterator().next();
        Room room = chatService.createRoom(new ArrayList<>(List.of(userTwo, newUser)), commonTopic);
        chatService.addRoom(room);
        chatService.removePendingUserById(userTwo.id());
        chatService.sendConnectMessage(userTwo, newUser.id(), commonTopic);
        chatService.sendConnectMessage(newUser, userTwo.id(), commonTopic);
    }

    @MessageMapping("/message")
    public void sendMessage(@Payload final Message message) {
        LOG.info("New message request: {}", message);
        chatService.sendMessage(message);
    }
}
