package hu.nye.chat.model;

import hu.nye.chat.enums.Gender;
import hu.nye.chat.enums.MessageType;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString
public final class Message {
    private MessageType type;
    private String senderId;

    private Gender senderGender;

    private String content;

    private String time;
    public Message(MessageType type, String senderId) {
        this.type = type;
        this.senderId = senderId;
    }
}


