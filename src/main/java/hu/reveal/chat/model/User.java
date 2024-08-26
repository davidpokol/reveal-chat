package hu.reveal.chat.model;

import hu.reveal.chat.enums.Gender;
import hu.reveal.chat.enums.Topic;

import java.util.ArrayList;

public record User(String id, String firstName, int age, int partnerMinAge, int partnerMaxAge, Gender gender,
                   Gender partnerGender, ArrayList<Topic> topics) {
}


