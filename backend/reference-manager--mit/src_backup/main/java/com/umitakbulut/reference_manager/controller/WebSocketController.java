package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.websocket.FlightUpdateMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/flight-update")
    @SendTo("/topic/flights")
    public FlightUpdateMessage handleFlightUpdate(FlightUpdateMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return message;
    }

    public void sendToUser(String username, FlightUpdateMessage message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/flight-updates", message);
    }

    public void broadcastFlightUpdate(FlightUpdateMessage message) {
        messagingTemplate.convertAndSend("/topic/flights", message);
    }


    public void sendToTopic(String topic, FlightUpdateMessage message) {
        messagingTemplate.convertAndSend("/topic/" + topic, message);
    }
} 