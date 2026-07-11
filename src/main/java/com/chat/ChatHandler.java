package com.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    private final Counter messagesReceived;
    private final Counter messagesBroadcast;

    private final ObjectMapper mapper = new ObjectMapper();

    public ChatHandler(MeterRegistry registry) {

        this.messagesReceived = Counter.builder("ws.messages.received")
                .description("Total de mensajes recibidos del cliente")
                .register(registry);

        this.messagesBroadcast = Counter.builder("ws.messages.broadcast")
                .description("Total de mensajes enviados a clientes")
                .register(registry);

        Gauge.builder("ws.sessions.active", sessions, Set::size)
                .description("Sesiones WebSocket activas en este momento")
                .register(registry);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        broadcast(new ChatMessage("system", "Un usuario se conectó", sessions.size()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            messagesReceived.increment();
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
                messagesBroadcast.increment();
            }
        }
    }
}
