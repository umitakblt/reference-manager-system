package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.FlightRequestDTO;
import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import com.umitakbulut.reference_manager.entity.FlightStatus;
import com.umitakbulut.reference_manager.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final FlightService flightService;

    @PostMapping("/add-test-flights")
    public ResponseEntity<String> addTestFlights() {
        try {
            log.info("🧪 Test uçuşları ekleniyor...");
            
            List<FlightResponseDTO> existingFlights = flightService.getAllFlights();
            log.info("📊 Mevcut uçuş sayısı: {}", existingFlights.size());
            
            for (int i = 6; i <= 10; i++) {
                FlightRequestDTO testFlight = new FlightRequestDTO();
                testFlight.setFlightNumber("TK00" + String.format("%02d", i));
                testFlight.setAirlineId(1L);
                testFlight.setOriginStationId(1L);
                testFlight.setDestinationStationId(2L);
                testFlight.setAircraftId(1L);
                testFlight.setStatus(FlightStatus.ON_TIME);
                
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime departure = now.plusHours(2 + new Random().nextInt(6));
                LocalDateTime arrival = departure.plusHours(1 + new Random().nextInt(2));
                
                testFlight.setScheduledDeparture(departure);
                testFlight.setScheduledArrival(arrival);
                
                flightService.addFlight(testFlight);
                log.info("✅ Test uçuşu eklendi: {}", testFlight.getFlightNumber());
            }
            
            return ResponseEntity.ok("Test uçuşları başarıyla eklendi! Mevcut uçuşlar korundu.");
            
        } catch (Exception e) {
            log.error("❌ Test uçuşları eklenirken hata: ", e);
            return ResponseEntity.badRequest().body("Test uçuşları eklenirken hata: " + e.getMessage());
        }
    }

    @PostMapping("/fix-flight-dates")
    public ResponseEntity<String> fixFlightDates() {
        try {
            log.info("🔧 Uçuş tarihleri düzeltiliyor...");
            
            List<FlightResponseDTO> flights = flightService.getAllFlights();
            int fixedCount = 0;
            
            for (FlightResponseDTO flight : flights) {
                boolean needsUpdate = false;
                FlightRequestDTO updateRequest = new FlightRequestDTO();
                
                updateRequest.setFlightNumber(flight.getFlightNumber());
                updateRequest.setAirlineId(flight.getAirlineId());
                updateRequest.setOriginStationId(flight.getOriginStationId());
                updateRequest.setDestinationStationId(flight.getDestinationStationId());
                updateRequest.setAircraftId(flight.getAircraftId());
                updateRequest.setStatus(flight.getStatus());
                
                if (flight.getScheduledDeparture() == null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime departure = now.plusHours(2 + new Random().nextInt(6));
                    updateRequest.setScheduledDeparture(departure);
                    needsUpdate = true;
                    log.info("🛫 {} uçuşu için kalkış zamanı eklendi: {}", 
                            flight.getFlightNumber(), departure);
                } else {
                    updateRequest.setScheduledDeparture(flight.getScheduledDeparture());
                }
                
                if (flight.getScheduledArrival() == null) {
                    LocalDateTime departure = updateRequest.getScheduledDeparture() != null ? 
                            updateRequest.getScheduledDeparture() : LocalDateTime.now().plusHours(3);
                    LocalDateTime arrival = departure.plusHours(1 + new Random().nextInt(2));
                    updateRequest.setScheduledArrival(arrival);
                    needsUpdate = true;
                    log.info("🛬 {} uçuşu için varış zamanı eklendi: {}", 
                            flight.getFlightNumber(), arrival);
                } else {
                    updateRequest.setScheduledArrival(flight.getScheduledArrival());
                }
                
                if (needsUpdate) {
                    flightService.updateFlight(flight.getId(), updateRequest);
                    fixedCount++;
                }
            }
            
            log.info("✅ {} uçuşun tarihi düzeltildi", fixedCount);
            return ResponseEntity.ok(String.format("%d uçuşun tarihi başarıyla düzeltildi!", fixedCount));
            
        } catch (Exception e) {
            log.error("❌ Uçuş tarihleri düzeltilirken hata: ", e);
            return ResponseEntity.badRequest().body("Uçuş tarihleri düzeltilirken hata: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("🚀 Backend çalışıyor! Test endpoint'leri hazır.");
    }
} 