package hu.reveal.chat.model;

import hu.reveal.chat.enums.Topic;

import java.util.ArrayList;

public record Room(String id, Topic topic, ArrayList<User> users) {
}


