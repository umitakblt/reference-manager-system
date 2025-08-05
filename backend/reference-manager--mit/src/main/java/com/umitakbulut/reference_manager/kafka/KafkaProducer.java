package com.umitakbulut.reference_manager.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import com.umitakbulut.reference_manager.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendFlight(Flight flight) {

        FlightResponseDTO flightDTO = convertFlightToDTO(flight);
        sendMessage("flight-topic", flightDTO);
    }

    public void sendFlightType(FlightType flightType) {
        sendMessage("flight-type-topic", flightType);
    }

    public void sendRole(Role role) {
        sendMessage("role-topic", role);
    }

    public void sendRoute(Route route) {
        sendMessage("route-topic", route);
    }

    public void sendUser(User user) {
        sendMessage("user-topic", user);
    }

    public void sendUserRole(UserRole userRole) {
        sendMessage("user-role-topic", userRole);
    }

    public void sendAirline(Airline airline) {
        sendMessage("airline-topic", airline);
    }

    public void sendAircraft(Aircraft aircraft) {
        sendMessage("aircraft-topic", aircraft);
    }

    public void sendStation(Station station) {
        sendMessage("station-topic", station);
    }

    public void sendMessage(String topic, Object data) {
        try {
            String message = objectMapper.writeValueAsString(data);
            kafkaTemplate.send(topic, message);
            log.info("Kafka mesajı gönderildi - Topic: {}, Mesaj: {}", topic, message);
        } catch (JsonProcessingException e) {
            log.error("Kafka mesajı serileştirilirken hata oluştu", e);
        }
    }


    private FlightResponseDTO convertFlightToDTO(Flight flight) {
        FlightResponseDTO dto = new FlightResponseDTO();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setScheduledDeparture(flight.getScheduledDeparture());
        dto.setScheduledArrival(flight.getScheduledArrival());
        dto.setStatus(flight.getStatus());
        dto.setDescription(flight.getDescription());
        
        if (flight.getAirline() != null) {
            dto.setAirlineId(flight.getAirline().getId());
            dto.setAirlineName(flight.getAirline().getName());
        }
        if (flight.getAircraft() != null) {
            dto.setAircraftId(flight.getAircraft().getId());
            dto.setAircraftModel(flight.getAircraft().getModel());
        }
        if (flight.getOriginStation() != null) {
            dto.setOriginStationId(flight.getOriginStation().getId());
            dto.setOriginStationName(flight.getOriginStation().getName());
            dto.setOriginStationCode(flight.getOriginStation().getCode());
        }
        if (flight.getDestinationStation() != null) {
            dto.setDestinationStationId(flight.getDestinationStation().getId());
            dto.setDestinationStationName(flight.getDestinationStation().getName());
            dto.setDestinationStationCode(flight.getDestinationStation().getCode());
        }
        if (flight.getFlightType() != null) {
            dto.setFlightTypeId(flight.getFlightType().getId());
            dto.setFlightTypeName(flight.getFlightType().getName());
        }
        
        return dto;
    }
}