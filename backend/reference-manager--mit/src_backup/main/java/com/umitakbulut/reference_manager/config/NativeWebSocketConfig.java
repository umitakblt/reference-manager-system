package com.umitakbulut.reference_manager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.umitakbulut.reference_manager.controller.NativeWebSocketController;

@Configuration
@EnableWebSocket
public class NativeWebSocketConfig implements WebSocketConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(NativeWebSocketConfig.class);
    
    private final NativeWebSocketController nativeWebSocketController;

    public NativeWebSocketConfig(NativeWebSocketController nativeWebSocketController) {
        this.nativeWebSocketController = nativeWebSocketController;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(nativeWebSocketController, "/websocket")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("http://localhost:5173");
        
        logger.info("Native WebSocket endpoint /websocket registered with CORS");
    }
} 