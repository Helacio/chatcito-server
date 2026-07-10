package com.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        broadcast(new ChatMessage("system", "Un usuario se conectó", sessions.size()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            ChatMessage incoming = mapper.readValue(message.getPayload(), ChatMessage.class);

            ChatMessage outgoing = new ChatMessage();
            // preserve type (e.g., 'message' or 'audio') sent by the client
            outgoing.setType(incoming.getType() != null ? incoming.getType() : "message");
            outgoing.setUsername(incoming.getUsername());
            outgoing.setText(incoming.getText());
            outgoing.setTimestamp(Instant.now().toString());

            broadcast(outgoing);
        } catch (Exception e) {
            System.err.println("Mensaje inválido: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        broadcast(new ChatMessage("system", "Un usuario se desconectó", sessions.size()));
    }

    private void broadcast(ChatMessage msg) throws Exception {
        String json = mapper.writeValueAsString(msg);
        TextMessage frame = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                synchronized (s) {
                    s.sendMessage(frame);
                }
            }
        }
    }
}
