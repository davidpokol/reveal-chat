package hu.nye.chat.service;

import hu.nye.chat.enums.Topic;
import hu.nye.chat.model.Message;
import hu.nye.chat.model.Room;
import hu.nye.chat.model.User;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;

public interface ChatService {

    void addPendingUser(User user);
    boolean removePendingUserById(String userId);
    Room createRoom(List<User> users, Topic topic);
    void addRoom(Room room);
    Optional<String> getRoomIdByUserId(String userId);
    void removeRoomById(String roomId);
    void sendMessage(Message message);
    void sendConnectMessage(User sender, String receiverId, Topic topic);
    void sendDisconnectMessage(String senderId);
    List<User> getPossiblePartnersWithTopic(User userOne);
    List<User> getPossiblePartnersWithNoTopic(User userOne);
    Pair<User,Topic> getMostAppropriatePartnerWithTopic(User userOne, List<User> possibleUsers);
}
