package com.umitakbulut.reference_manager.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final String topicName = "reference.events";

    public void publishEvent(ReferenceChangedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicName, event.getEntity() + "-" + event.getEntityId(), message);
            logger.info(" Kafka'ya gönderildi → {}", message);
        } catch (JsonProcessingException e) {
            logger.error(" Kafka event serileştirme hatası", e);
        }
    }
}
