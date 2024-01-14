package hu.nye.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MessageArea {

    private final List<Room> rooms;
    private final List<User> pendingUsers;

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void addUser(User user) {
        pendingUsers.add(user);
    }

    public void removeUser(User user) {
        pendingUsers.remove(user);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }
}
