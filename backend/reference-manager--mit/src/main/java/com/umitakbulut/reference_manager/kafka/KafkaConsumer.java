package com.umitakbulut.reference_manager.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umitakbulut.reference_manager.dto.request.FlightTypeRequestDTO;
import com.umitakbulut.reference_manager.dto.request.RoleRequestDTO;
import com.umitakbulut.reference_manager.dto.request.RouteRequestDTO;
import com.umitakbulut.reference_manager.entity.*;
import com.umitakbulut.reference_manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final FlightArchiveService archiveService;
    private final RoleService roleService;
    private final RouteService routeService;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final FlightTypeService flightTypeService;

    @KafkaListener(topics = "flight-topic", groupId = "flight-group")
    public void consume(String message) {
        try {
            Flight flight = objectMapper.readValue(message, Flight.class);
            archiveService.saveFromFlight(flight);
            log.info("Kafka'dan alındı ve archive'e kaydedildi: {}", message);
        } catch (Exception e) {
            log.error("Mesaj tüketilirken hata oluştu", e);
        }
    }

    @KafkaListener(topics = "role-topic", groupId = "role-group")
    public void consumeRole(String message) {
        try {
            Role role = objectMapper.readValue(message, Role.class);
            RoleRequestDTO requestDTO = new RoleRequestDTO();
            requestDTO.setName(role.getName());
            requestDTO.setDescription(role.getDescription());
            roleService.save(requestDTO);
            log.info("Role mesajı alındı: {}", message);
        } catch (Exception e) {
            log.error("Role mesajı işlenirken hata oluştu", e);
        }
    }

    @KafkaListener(topics = "route-topic", groupId = "route-group")
    public void consumeRoute(String message) {
        try {
            Route route = objectMapper.readValue(message, Route.class);
            RouteRequestDTO requestDTO = new RouteRequestDTO();
            requestDTO.setOriginStationId(route.getOriginStation().getId());
            requestDTO.setDestinationStationId(route.getDestinationStation().getId());
            requestDTO.setDistance(route.getDistance());
            routeService.saveFromKafka(requestDTO);
            log.info("Route mesajı alındı: {}", message);
        } catch (Exception e) {
            log.error("Route mesajı işlenirken hata oluştu", e);
        }
    }

    @KafkaListener(topics = "user-topic", groupId = "user-group")
    public void consumeUser(String message) {
        try {
            User user = objectMapper.readValue(message, User.class);
            userService.saveFromKafka(user); // bu metodu UserServiceImpl'e ekle
            log.info("User mesajı alındı: {}", message);
        } catch (Exception e) {
            log.error("User mesajı işlenirken hata oluştu", e);
        }
    }

    @KafkaListener(topics = "user-role-topic", groupId = "user-role-group")
    public void consumeUserRole(String message) {
        try {
            UserRole userRole = objectMapper.readValue(message, UserRole.class);
            userRoleService.saveFromKafka(userRole); // bu metodu service'e ekle
            log.info("UserRole mesajı alındı: {}", message);
        } catch (Exception e) {
            log.error("UserRole mesajı işlenirken hata oluştu", e);
        }
    }

    @KafkaListener(topics = "flight-type-topic", groupId = "flight-type-group")
    public void consumeFlightType(String message) {
        try {
            FlightType flightType = objectMapper.readValue(message, FlightType.class);
            flightTypeService.saveFromKafka(flightType);
            log.info("FlightType mesajı alındı: {}", message);
        } catch (Exception e) {
            log.error("FlightType mesajı işlenirken hata oluştu", e);
        }
    }
}