package hu.reveal.chat.service.impl;

import hu.reveal.chat.enums.MessageType;
import hu.reveal.chat.enums.Topic;
import hu.reveal.chat.model.Message;
import hu.reveal.chat.model.MessageArea;
import hu.reveal.chat.model.Room;
import hu.reveal.chat.model.User;
import hu.reveal.chat.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static hu.reveal.chat.service.util.ChatServiceUtil.getFilteredUsersByTopics;
import static hu.reveal.chat.service.util.ChatServiceUtil.getRandomTopic;
import static hu.reveal.chat.service.util.ChatServiceUtil.getUserToAge;
import static hu.reveal.chat.service.util.ChatServiceUtil.isMatchAgeRequests;
import static hu.reveal.chat.service.util.ChatServiceUtil.isMatchGenderRequests;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final MessageArea messageArea;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatServiceImpl(MessageArea messageArea,
                           SimpMessagingTemplate simpMessagingTemplate) {
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
        Optional<User> userToRemove = messageArea.pendingUsers().stream()
                .filter(user -> user.id().equals(userId))
                .findFirst();

        if (userToRemove.isPresent()) {
            messageArea.removeUser(userToRemove.get());
            LOG.info("User removed from pending: {}", userId);
            return true;
        }
        return false;
    }

    @Override
    public Room createRoom(final ArrayList<User> users, final Topic topic) {

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

        return messageArea.rooms()
                .stream()
                .filter(room -> room.users().stream().anyMatch(user -> user.id().equals(userId)))
                .map(Room::id)
                .findFirst();
    }

    @Override
    public void removeRoomById(final String roomId) {

        Optional<Room> roomToRemove = messageArea.rooms().stream()
                .filter(room -> room.id().equals(roomId))
                .findFirst();

        roomToRemove.ifPresent(messageArea::removeRoom);
    }

    @Override
    public void sendMessage(Message message) {
        Optional<String> receiverId = getReceiverId(message.getSenderId());

        if (receiverId.isPresent()) {
            simpMessagingTemplate.convertAndSendToUser(receiverId.get(), "/messages", message);
            LOG.info("New message sent: message: {} receiverId: {}", message, receiverId);
        }
    }

    @Override
    public void sendConnectMessage(User sender, String receiverId, Topic topic) {

        simpMessagingTemplate.convertAndSendToUser(receiverId, "/messages",
                new Message(
                        MessageType.CONNECT,
                        sender.id(),
                        sender.gender(),
                        topic.toString(),
                        new Date().toString())
        );
    }

    @Override
    public void sendDisconnectMessage(String senderId) {

        Optional<String> receiverId = getReceiverId(senderId);

        if (receiverId.isEmpty()) { // If a user wants to send a message to an abandoned room
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
    public ArrayList<User> getPossiblePartnersWithNoTopic(User userOne) {
        ArrayList<User> possibleUsers = new ArrayList<>();
        for (User pendingUser : messageArea.pendingUsers()) {
            if (pendingUser.topics().isEmpty()
                    && isMatchAgeRequests(userOne, pendingUser)
                    && isMatchGenderRequests(userOne, pendingUser)) {
                possibleUsers.add(pendingUser);
            }
        }
        return possibleUsers;
    }

    @Override
    public ArrayList<User> getPossiblePartnersWithTopic(User userOne) {

        ArrayList<User> possiblePartners = new ArrayList<>();

        for (User pendingUser : messageArea.pendingUsers()) {

            for (Topic pendingUserTopic : pendingUser.topics()) {
                for (Topic newUserTopic : userOne.topics()) {
                    if (pendingUserTopic.equals(newUserTopic)
                            && isMatchAgeRequests(userOne, pendingUser)
                            && isMatchGenderRequests(userOne, pendingUser)) {
                        possiblePartners.add(pendingUser);
                        break;
                    }
                }
            }
        }
        return possiblePartners;
    }

    @Override
    public HashMap<User, Topic> getMostAppropriatePartnerWithTopic(User userOne, ArrayList<User> possibleUsers) {

        if (userOne.topics().isEmpty()) {
            return new HashMap<>() {
                {
                    put(getUserToAge(userOne.age(), possibleUsers).get(), Topic.NONE);
                }
            };
        }

        ArrayList<User> filteredByTopics = getFilteredUsersByTopics(userOne.topics(), possibleUsers);
        User userTwo;
        if (filteredByTopics.size() == 1) {
            userTwo = filteredByTopics.get(0);
        } else {
            userTwo = getUserToAge(userOne.age(), filteredByTopics).get();
        }

        Topic randomTopic = getRandomTopic(userOne, userTwo);
        return new HashMap<>() {
            {
                put(userTwo, randomTopic);
            }
        };
    }

    private Optional<String> getReceiverId(String sender) {

        for (Room room : messageArea.rooms()) {

            if (room.users().size() != 2) {
                continue;
            }

            if (sender.equals(room.users().get(0).id())) {
                return Optional.ofNullable(room.users().get(1).id());
            }

            if (sender.equals(room.users().get(1).id())) {
                return Optional.ofNullable(room.users().get(0).id());
            }
        }
        return Optional.empty();
    }
}
