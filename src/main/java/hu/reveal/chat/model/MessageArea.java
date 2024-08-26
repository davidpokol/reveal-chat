package hu.reveal.chat.model;

import java.util.ArrayList;

public record MessageArea(ArrayList<Room> rooms, ArrayList<User> pendingUsers) {

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
