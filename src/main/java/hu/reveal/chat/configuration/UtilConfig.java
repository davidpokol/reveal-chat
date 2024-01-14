package hu.nye.chat.configuration;

import hu.nye.chat.service.util.ChatServiceImplUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfig {

    @Bean
    public ChatServiceImplUtil chatServiceImplUtil() {
        return new ChatServiceImplUtil();
    }
}
