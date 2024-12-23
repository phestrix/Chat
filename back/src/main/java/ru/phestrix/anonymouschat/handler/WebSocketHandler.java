package ru.phestrix.anonymouschat.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = session.getAttributes().get("username").toString();
        if (username != null) {
            activeSessions.put(username, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = session.getAttributes().get("username").toString();
        if (username != null) {
            activeSessions.remove(username);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String[] parts = payload.split(":", 2); // format : "recipient:msg"
        if (parts.length == 2) {
            String recipient = parts[0];
            String msg = parts[1];
            WebSocketSession recipientSession = activeSessions.get(recipient);
            if (recipient != null && recipientSession.isOpen()) {
                recipientSession.sendMessage(new TextMessage(msg));
            }
        }
    }
}
