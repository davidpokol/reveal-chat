package hu.nye.chat.configuration;

import hu.nye.chat.model.MessageArea;
import hu.nye.chat.model.Room;
import hu.nye.chat.model.User;
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
