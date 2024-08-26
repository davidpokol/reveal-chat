package hu.reveal.chat.controller;

import hu.reveal.chat.service.impl.ChatServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Component
public class WebSocketEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final ChatServiceImpl chatService;

    @Autowired
    public WebSocketEventListener(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Optional<String> userId = Optional.ofNullable((String) headerAccessor.getSessionAttributes().get("userId"));
        if (userId.isEmpty()) {
            LOG.error("Unknown user tried to disconnect");
            return;
        }
        // userId is not null

        Optional<String> roomId = chatService.getRoomIdByUserId(userId.get());

        if (roomId.isEmpty()) {
            // userId not null but roomId has null value

            if (chatService.removePendingUserById(userId.get())) {
                LOG.info("User disconnected from pending: userId: {}", userId);
            }
            return;
        }
        // neither userId nor roomId is null

        LOG.info("User disconnected from room: userId: {} roomId: {}", userId, roomId.get());
        chatService.sendDisconnectMessage(userId.get());
        chatService.removeRoomById(roomId.get());
        LOG.info("Abandoned room deleted: roomId:{}", roomId.get());
    }
}

