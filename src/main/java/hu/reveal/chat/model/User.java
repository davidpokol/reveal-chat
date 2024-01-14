package hu.nye.chat.model;

import hu.nye.chat.enums.Gender;
import hu.nye.chat.enums.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public final class User {

    private final String id;
    private final String firstName;
    private final int age;
    private final int partnerMinAge;
    private final int partnerMaxAge;
    private final Gender gender;
    private final Gender partnerGender;
    private final List<Topic> topics;
}


