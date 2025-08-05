package com.umitakbulut.reference_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NativeWebSocketController extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NativeWebSocketController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket bağlantısı kuruldu: {}", session.getId());
        sessions.put(session.getId(), session);
        
        try {
            Thread.sleep(100);
            if (session.isOpen()) {
                sendMessage(session, "CONNECTION_ESTABLISHED", "WebSocket bağlantısı başarılı");
            }
        } catch (Exception e) {
            logger.warn("Bağlantı kurulduktan sonra mesaj gönderilemedi: {}", e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            logger.info("WebSocket mesajı alındı: {}", payload);
            
            MessageData messageData = objectMapper.readValue(payload, MessageData.class);
            
            switch (messageData.getType()) {
                case "FLIGHT_UPDATE":
                    handleFlightUpdate(session, messageData);
                    break;
                case "PING":
                    sendMessage(session, "PONG", "pong");
                    break;
                default:
                    logger.warn("Bilinmeyen mesaj tipi: {}", messageData.getType());
            }
            
        } catch (Exception e) {
            logger.error("Mesaj işleme hatası: {}", e.getMessage());
            sendMessage(session, "ERROR", "Mesaj işlenemedi: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket bağlantısı kapandı: {} - {}", session.getId(), status);
        sessions.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport hatası: {}", exception.getMessage());
        
        if (exception instanceof IOException) {
            logger.debug("IO hatası - bağlantı muhtemelen kapatıldı: {}", exception.getMessage());
        } else {
            logger.error("Beklenmeyen transport hatası", exception);
        }
        
        sessions.remove(session.getId());
    }

    private void handleFlightUpdate(WebSocketSession session, MessageData messageData) {
        try {
            logger.info("Flight update işleniyor: {}", messageData.getData());
            
            broadcastMessage("FLIGHT_UPDATE", messageData.getData());
            
            sendMessage(session, "FLIGHT_UPDATE_CONFIRMED", "Flight update başarıyla işlendi");
            
        } catch (Exception e) {
            logger.error("Flight update işleme hatası: {}", e.getMessage());
            sendMessage(session, "ERROR", "Flight update işlenemedi: " + e.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session, String type, Object data) {
        if (session == null || !session.isOpen()) {
            logger.debug("Session null veya kapalı, mesaj gönderilemiyor");
            return;
        }
        
        try {
            MessageData message = new MessageData();
            message.setType(type);
            message.setData(data);
            message.setTimestamp(new java.util.Date().toInstant().toString());
            
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
            
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("Broken pipe")) {
                logger.debug("Broken pipe hatası - bağlantı kapatılmış: {}", e.getMessage());
            } else {
                logger.error("Mesaj gönderme hatası: {}", e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Beklenmeyen mesaj gönderme hatası: {}", e.getMessage());
        }
    }

    private void broadcastMessage(String type, Object data) {
        logger.info("=== broadcastMessage çağrıldı ===");
        logger.info("Type: {}", type);
        logger.info("Data: {}", data);
        logger.info("Session sayısı: {}", sessions.size());
        
        sessions.values().removeIf(session -> !session.isOpen());
        
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                logger.info("Session {} açık, mesaj gönderiliyor", session.getId());
                sendMessage(session, type, data);
            } else {
                logger.info("Session {} kapalı", session.getId());
            }
        });
        
        logger.info("=== broadcastMessage tamamlandı ===");
    }

    public void broadcastFlightUpdate(String action, Object flight) {
        logger.info("=== broadcastFlightUpdate çağrıldı ===");
        logger.info("Action: {}", action);
        logger.info("Flight: {}", flight);
        logger.info("Aktif session sayısı: {}", sessions.size());
        
        sessions.keySet().forEach(sessionId -> {
            logger.info("Aktif session ID: {}", sessionId);
        });
        
        FlightUpdateData flightUpdateData = new FlightUpdateData(action, flight);
        
        logger.info("Flight update data: {}", flightUpdateData);
        broadcastMessage("FLIGHT_UPDATE", flightUpdateData);
        logger.info("=== broadcastFlightUpdate tamamlandı ===");
    }

    public static class MessageData {
        private String type;
        private Object data;
        private String timestamp;
        private String connectionId;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getConnectionId() { return connectionId; }
        public void setConnectionId(String connectionId) { this.connectionId = connectionId; }
    }

    public static class FlightUpdateData {
        private String action;
        private Object flight;

        public FlightUpdateData(String action, Object flight) {
            this.action = action;
            this.flight = flight;
        }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public Object getFlight() { return flight; }
        public void setFlight(Object flight) { this.flight = flight; }
    }
} 