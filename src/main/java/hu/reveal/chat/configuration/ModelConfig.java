package hu.reveal.chat.configuration;

import hu.reveal.chat.model.MessageArea;
import hu.reveal.chat.model.Room;
import hu.reveal.chat.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class ModelConfig {

    @Bean
    public MessageArea messageArea() {
        return new MessageArea(new ArrayList<Room>(), new ArrayList<User>());
    }
}
