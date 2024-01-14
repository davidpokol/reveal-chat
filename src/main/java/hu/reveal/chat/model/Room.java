package hu.nye.chat.model;

import hu.nye.chat.enums.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public final class Room {

    private final String id;
    private final Topic topic;
    private final List<User> users;
}


