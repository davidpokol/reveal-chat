package hu.nye.chat.service.impl;

import hu.nye.chat.enums.MessageType;
import hu.nye.chat.enums.Topic;
import hu.nye.chat.model.*;
import hu.nye.chat.service.ChatService;
import hu.nye.chat.service.util.ChatServiceImplUtil;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final ChatServiceImplUtil serviceUtil;
    private final MessageArea messageArea;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatServiceImpl(ChatServiceImplUtil serviceUtil,
                           MessageArea messageArea,
                           SimpMessagingTemplate simpMessagingTemplate) {
        this.serviceUtil = serviceUtil;
        this.messageArea = messageArea;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void addPendingUser(final User user) {
        messageArea.addUser(user);
        LOG.info("User added to pending: {}", user);
    }
    @Override
    public boolean removePendingUserById(final String userId) {
        Optional<User> userToRemove = messageArea.getPendingUsers().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();

        if (userToRemove.isPresent()) {
            messageArea.removeUser(userToRemove.get());
            LOG.info("User removed from pending: {}", userId);
            return true;
        }
        return false;
    }

    @Override
    public Room createRoom(final List<User> users, final Topic topic) {

        String roomId = UUID.randomUUID().toString();
        return new Room(roomId, topic, users);
    }

    @Override
    public void addRoom(final Room room) {
        messageArea.addRoom(room);
        LOG.info("New chat room added: {}", room);
    }

    @Override
    public Optional<String> getRoomIdByUserId(String userId) {

        return messageArea.getRooms()
                .stream()
                .filter(room -> room.getUsers().stream().anyMatch(user -> user.getId().equals(userId)))
                .map(Room::getId)
                .findFirst();
    }

    @Override
    public void removeRoomById(final String roomId) {

        Optional<Room> roomToRemove = messageArea.getRooms().stream()
                .filter(room -> room.getId().equals(roomId))
                .findFirst();

        roomToRemove.ifPresent(messageArea::removeRoom);
    }

    @Override
    public void sendMessage(Message message) {
        Optional<String> receiverId = getReceiverId(message.getSenderId());

        if(receiverId.isPresent()) {
            simpMessagingTemplate.convertAndSendToUser(receiverId.get(), "/messages", message);
            LOG.info("New message sent: message: {} receiverId: {}", message, receiverId);
        }
    }
    @Override
    public void sendConnectMessage(User sender, String receiverId, Topic topic) {

        simpMessagingTemplate.convertAndSendToUser(receiverId, "/messages",
                new Message(
                        MessageType.CONNECT,
                        sender.getId(),
                        sender.getGender(),
                        topic.toString(),
                        new Date().toString())
        );
    }

    @Override
    public void sendDisconnectMessage(String senderId) {

        Optional<String> receiverId = getReceiverId(senderId);

        if(receiverId.isEmpty()) { // If a user wants to send a message to an abandoned room
            return;
        }
        simpMessagingTemplate.convertAndSendToUser(
                receiverId.get(), "/messages",
                new Message(
                        MessageType.DISCONNECT,
                        senderId)
        );
    }

    @Override
    public List<User> getPossiblePartnersWithNoTopic(User userOne) {
        List<User> possibleUsers = new ArrayList<>();
            for (User pendingUser : messageArea.getPendingUsers()) {
                if (pendingUser.getTopics().isEmpty()
                        && serviceUtil.isMatchAgeRequests(userOne, pendingUser)
                        && serviceUtil.isMatchGenderRequests(userOne, pendingUser)) {
                    possibleUsers.add(pendingUser);
                }
            }
            return possibleUsers;
    }
    @Override
    public List<User> getPossiblePartnersWithTopic(User userOne) {

        List<User> possiblePartners = new ArrayList<>();

        for (User pendingUser : messageArea.getPendingUsers()) {

            for (Topic pendingUserTopic : pendingUser.getTopics()) {
                for (Topic newUserTopic : userOne.getTopics()) {
                    if (pendingUserTopic.equals(newUserTopic)
                            && serviceUtil.isMatchAgeRequests(userOne, pendingUser)
                            && serviceUtil.isMatchGenderRequests(userOne, pendingUser)) {
                        possiblePartners.add(pendingUser);
                        break;
                    }
                }
            }
        }
        return possiblePartners;
    }

    @Override
    public Pair<User,Topic> getMostAppropriatePartnerWithTopic(User userOne, List<User> possibleUsers) {

        if (userOne.getTopics().isEmpty()) {
            return new Pair<>(serviceUtil.getUserToAge(userOne.getAge(), possibleUsers).get(), Topic.NONE);
        }

        List<User> filteredByTopics = serviceUtil.getFilteredUsersByTopics(userOne.getTopics(), possibleUsers);
        User userTwo;
        if (filteredByTopics.size() == 1) {
            userTwo = filteredByTopics.get(0);
        } else {
            userTwo = serviceUtil.getUserToAge(userOne.getAge(), filteredByTopics).get();
        }

        Topic randomTopic = serviceUtil.getRandomTopic(userOne, userTwo);
        return new Pair<>(userTwo, randomTopic);
    }
    private Optional<String> getReceiverId(String sender) {

        for (Room room : messageArea.getRooms()) {

            if (room.getUsers().size() != 2) {
                continue;
            }

            if (sender.equals(room.getUsers().get(0).getId())) {
                return Optional.ofNullable(room.getUsers().get(1).getId());
            }

            if (sender.equals(room.getUsers().get(1).getId())) {
                return Optional.ofNullable(room.getUsers().get(0).getId());
            }
        }
        return Optional.empty();
    }
}
