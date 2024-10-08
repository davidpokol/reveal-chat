package hu.reveal.chat.service;

import hu.reveal.chat.enums.Topic;
import hu.reveal.chat.model.Message;
import hu.reveal.chat.model.Room;
import hu.reveal.chat.model.User;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public interface ChatService {

    void addPendingUser(User user);

    boolean removePendingUserById(String userId);

    Room createRoom(ArrayList<User> users, Topic topic);

    void addRoom(Room room);

    Optional<String> getRoomIdByUserId(String userId);

    void removeRoomById(String roomId);

    void sendMessage(Message message);

    void sendConnectMessage(User sender, String receiverId, Topic topic);

    void sendDisconnectMessage(String senderId);

    ArrayList<User> getPossiblePartnersWithTopic(User userOne);

    ArrayList<User> getPossiblePartnersWithNoTopic(User userOne);

    Map<User, Topic> getMostAppropriatePartnerWithTopic(User userOne, ArrayList<User> possibleUsers);
}
