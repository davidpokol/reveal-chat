package hu.reveal.chat.model;

import hu.reveal.chat.enums.Gender;
import hu.reveal.chat.enums.MessageType;

import java.util.Objects;

public final class Message {
    private MessageType type;
    private String senderId;
    private Gender senderGender;
    private String content;
    private String time;

    public Message() {
    }

    public Message(MessageType type, String senderId) {
        this.type = type;
        this.senderId = senderId;
    }

    public Message(MessageType type, String senderId, Gender senderGender, String content, String time) {
        this.type = type;
        this.senderId = senderId;
        this.senderGender = senderGender;
        this.content = content;
        this.time = time;
    }

    public MessageType getType() {
        return type;
    }

    public String getSenderId() {
        return senderId;
    }

    public Gender getSenderGender() {
        return senderGender;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return type == message.type &&
                Objects.equals(senderId, message.senderId) &&
                senderGender == message.senderGender &&
                Objects.equals(content, message.content) &&
                Objects.equals(time, message.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, senderId, senderGender, content, time);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", senderId='" + senderId + '\'' +
                ", senderGender=" + senderGender +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}


