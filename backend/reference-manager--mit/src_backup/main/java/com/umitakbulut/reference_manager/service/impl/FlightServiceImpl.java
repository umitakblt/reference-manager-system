package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.dto.request.FlightRequestDTO;
import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import com.umitakbulut.reference_manager.entity.*;
import com.umitakbulut.reference_manager.exception.NotFoundException;
import com.umitakbulut.reference_manager.kafka.KafkaProducer;
import com.umitakbulut.reference_manager.mapper.FlightMapper;
import com.umitakbulut.reference_manager.repository.*;
import com.umitakbulut.reference_manager.service.FlightService;
import com.umitakbulut.reference_manager.service.FlightArchiveService;
import com.umitakbulut.reference_manager.controller.NativeWebSocketController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final AirlineRepository airlineRepository;
    private final AircraftRepository aircraftRepository;
    private final StationRepository stationRepository;
    private final FlightTypeRepository flightTypeRepository;
    private final KafkaProducer kafkaProducer;
    
    private final NativeWebSocketController nativeWebSocketController;
    private final FlightArchiveService flightArchiveService;

    @Override
    public FlightResponseDTO addFlight(FlightRequestDTO requestDTO) {
        return createFlight(requestDTO);
    }

    @Override
    public FlightResponseDTO createFlight(FlightRequestDTO flightRequestDTO) {
        Flight flight = flightMapper.toEntity(flightRequestDTO);
        
        validateFlightTimes(flight);
        
        if (flightRequestDTO.getAirlineId() != null) {
            Airline airline = airlineRepository.findById(flightRequestDTO.getAirlineId())
                    .orElseThrow(() -> new NotFoundException("Airline not found"));
            flight.setAirline(airline);
        }
        
        if (flightRequestDTO.getAircraftId() != null) {
            Aircraft aircraft = aircraftRepository.findById(flightRequestDTO.getAircraftId())
                    .orElseThrow(() -> new NotFoundException("Aircraft not found"));
            flight.setAircraft(aircraft);
        }
        
        if (flightRequestDTO.getOriginStationId() != null) {
            Station originStation = stationRepository.findById(flightRequestDTO.getOriginStationId())
                    .orElseThrow(() -> new NotFoundException("Origin station not found"));
            flight.setOriginStation(originStation);
        }
        
        if (flightRequestDTO.getDestinationStationId() != null) {
            Station destinationStation = stationRepository.findById(flightRequestDTO.getDestinationStationId())
                    .orElseThrow(() -> new NotFoundException("Destination station not found"));
            flight.setDestinationStation(destinationStation);
        }
        
        if (flightRequestDTO.getFlightTypeId() != null) {
            FlightType flightType = flightTypeRepository.findById(flightRequestDTO.getFlightTypeId())
                    .orElseThrow(() -> new NotFoundException("Flight type not found"));
            flight.setFlightType(flightType);
        }
        
        Flight savedFlight = flightRepository.save(flight);
        
        kafkaProducer.sendFlight(savedFlight);
        
        sendFlightUpdateMessage("CREATE", savedFlight, "Yeni uçuş oluşturuldu");
        
        try {
            flightArchiveService.saveFromFlight(savedFlight);
            System.out.println("Uçuş otomatik olarak arşivlendi: " + savedFlight.getFlightNumber());
        } catch (Exception e) {
            System.err.println("Otomatik arşivleme hatası: " + e.getMessage());
        }
        
        return flightMapper.toDto(savedFlight);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponseDTO getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight not found"));
        return flightMapper.toDto(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> getAllFlights() {
        List<Flight> flights = flightRepository.findAllWithRelations();
        return flightMapper.toDtoList(flights);
    }

    @Override
    public FlightResponseDTO updateFlight(Long id, FlightRequestDTO flightRequestDTO) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight not found"));
        
        Flight updatedFlight = flightMapper.toEntity(flightRequestDTO);
        updatedFlight.setId(existingFlight.getId());
        
        // İlişkili entity'leri set et
        if (flightRequestDTO.getAirlineId() != null) {
            Airline airline = airlineRepository.findById(flightRequestDTO.getAirlineId())
                    .orElseThrow(() -> new NotFoundException("Airline not found"));
            updatedFlight.setAirline(airline);
        } else {
            updatedFlight.setAirline(existingFlight.getAirline());
        }
        
        if (flightRequestDTO.getAircraftId() != null) {
            Aircraft aircraft = aircraftRepository.findById(flightRequestDTO.getAircraftId())
                    .orElseThrow(() -> new NotFoundException("Aircraft not found"));
            updatedFlight.setAircraft(aircraft);
        } else {
            updatedFlight.setAircraft(existingFlight.getAircraft());
        }
        
        if (flightRequestDTO.getOriginStationId() != null) {
            Station originStation = stationRepository.findById(flightRequestDTO.getOriginStationId())
                    .orElseThrow(() -> new NotFoundException("Origin station not found"));
            updatedFlight.setOriginStation(originStation);
        } else {
            updatedFlight.setOriginStation(existingFlight.getOriginStation());
        }
        
        if (flightRequestDTO.getDestinationStationId() != null) {
            Station destinationStation = stationRepository.findById(flightRequestDTO.getDestinationStationId())
                    .orElseThrow(() -> new NotFoundException("Destination station not found"));
            updatedFlight.setDestinationStation(destinationStation);
        } else {
            updatedFlight.setDestinationStation(existingFlight.getDestinationStation());
        }
        
        if (flightRequestDTO.getFlightTypeId() != null) {
            FlightType flightType = flightTypeRepository.findById(flightRequestDTO.getFlightTypeId())
                    .orElseThrow(() -> new NotFoundException("Flight type not found"));
            updatedFlight.setFlightType(flightType);
        } else {
            updatedFlight.setFlightType(existingFlight.getFlightType());
        }
        
        // Tarih validasyonu
        validateFlightTimes(updatedFlight);
        
        Flight savedFlight = flightRepository.save(updatedFlight);
        
        kafkaProducer.sendFlight(savedFlight);
        
        // WebSocket ile uçuş güncelleme mesajı gönder
        sendFlightUpdateMessage("UPDATE", savedFlight, "Uçuş güncellendi");
        
        // Lazy loading sorununu önlemek için manuel DTO oluştur
        FlightResponseDTO responseDTO = new FlightResponseDTO();
        responseDTO.setId(savedFlight.getId());
        responseDTO.setFlightNumber(savedFlight.getFlightNumber());
        responseDTO.setScheduledDeparture(savedFlight.getScheduledDeparture());
        responseDTO.setScheduledArrival(savedFlight.getScheduledArrival());
        responseDTO.setStatus(savedFlight.getStatus());
        responseDTO.setDescription(savedFlight.getDescription());
        
        // İlişkili entity'lerin ID'lerini set et
        if (savedFlight.getAirline() != null) {
            responseDTO.setAirlineId(savedFlight.getAirline().getId());
            responseDTO.setAirlineName("Airline-" + savedFlight.getAirline().getId());
        }
        if (savedFlight.getAircraft() != null) {
            responseDTO.setAircraftId(savedFlight.getAircraft().getId());
            responseDTO.setAircraftModel("Aircraft-" + savedFlight.getAircraft().getId());
        }
        if (savedFlight.getOriginStation() != null) {
            responseDTO.setOriginStationId(savedFlight.getOriginStation().getId());
            responseDTO.setOriginStationName("Station-" + savedFlight.getOriginStation().getId());
        }
        if (savedFlight.getDestinationStation() != null) {
            responseDTO.setDestinationStationId(savedFlight.getDestinationStation().getId());
            responseDTO.setDestinationStationName("Station-" + savedFlight.getDestinationStation().getId());
        }
        if (savedFlight.getFlightType() != null) {
            responseDTO.setFlightTypeName("Type-" + savedFlight.getFlightType().getId());
        }
        
        return responseDTO;
    }

    @Override
    public void deleteFlight(Long id) {
        System.out.println("=== deleteFlight çağrıldı ===");
        System.out.println("Flight ID: " + id);
        
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight not found"));
        
        System.out.println("Flight bulundu: " + flight.getFlightNumber());
        System.out.println("nativeWebSocketController null mu: " + (nativeWebSocketController == null));
        
        // WebSocket ile uçuş silme mesajı gönder
        sendFlightUpdateMessage("DELETE", flight, "Uçuş silindi");
        
        flightRepository.deleteById(id);
        System.out.println("=== deleteFlight tamamlandı ===");
    }

    @Override
    @Transactional
    public void archiveFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight not found"));
        
        // FlightArchiveService'i kullanarak arşivle
        flightArchiveService.saveFromFlight(flight);
        
        // WebSocket ile arşivleme mesajı gönder
        sendFlightUpdateMessage("ARCHIVE", flight, "Uçuş arşivlendi");
        
        // Uçuşu silme - sadece arşivle
        // flightRepository.deleteById(id);
    }
    
    /**
     * Uçuş zamanlarını validasyon eder
     * Varış zamanı kalkış zamanından sonra olmalıdır
     */
    private void validateFlightTimes(Flight flight) {
        if (flight.getScheduledDeparture() != null && flight.getScheduledArrival() != null) {
            if (!flight.getScheduledArrival().isAfter(flight.getScheduledDeparture())) {
                throw new IllegalArgumentException("Varış zamanı kalkış zamanından sonra olmalıdır!");
            }
        }
    }



    @Override
    public void bulkUploadFlights(MultipartFile file) {
        // Şimdilik basit bir implementasyon
        // Daha sonra CSV parsing ve toplu kaydetme mantığı eklenebilir
        try {
            // Dosya işleme mantığı burada olacak
            System.out.println("Bulk upload işlemi başlatıldı: " + file.getOriginalFilename());
        } catch (Exception e) {
            throw new RuntimeException("Bulk upload işlemi başarısız: " + e.getMessage());
        }
    }

    private void sendFlightUpdateMessage(String action, Flight flight, String message) {
        try {
            System.out.println("=== WebSocket Mesajı Gönderiliyor ===");
            System.out.println("Action: " + action);
            System.out.println("Flight ID: " + flight.getId());
            System.out.println("Flight Number: " + flight.getFlightNumber());
            
            // Lazy loading sorununu önlemek için basit bir DTO oluştur
            FlightWebSocketDTO flightDto = new FlightWebSocketDTO();
            flightDto.setId(flight.getId());
            flightDto.setFlightNumber(flight.getFlightNumber());
            flightDto.setScheduledDeparture(flight.getScheduledDeparture());
            flightDto.setScheduledArrival(flight.getScheduledArrival());
            flightDto.setStatus(flight.getStatus());
            flightDto.setDescription(flight.getDescription());
            
            // İlişkili entity'lerin ID'lerini set et - null kontrolü yap
            try {
                if (flight.getAirline() != null) {
                    flightDto.setAirlineId(flight.getAirline().getId());
                    // getName() çağrısından kaçın, sadece ID kullan
                    flightDto.setAirlineName("Airline-" + flight.getAirline().getId());
                }
            } catch (Exception e) {
                System.out.println("Airline bilgisi alınamadı: " + e.getMessage());
                flightDto.setAirlineName("Unknown");
            }
            
            try {
                if (flight.getAircraft() != null) {
                    flightDto.setAircraftId(flight.getAircraft().getId());
                    flightDto.setAircraftModel("Aircraft-" + flight.getAircraft().getId());
                }
            } catch (Exception e) {
                System.out.println("Aircraft bilgisi alınamadı: " + e.getMessage());
                flightDto.setAircraftModel("Unknown");
            }
            
            try {
                if (flight.getOriginStation() != null) {
                    flightDto.setOriginStationId(flight.getOriginStation().getId());
                    flightDto.setOriginStationName("Station-" + flight.getOriginStation().getId());
                }
            } catch (Exception e) {
                System.out.println("Origin station bilgisi alınamadı: " + e.getMessage());
                flightDto.setOriginStationName("Unknown");
            }
            
            try {
                if (flight.getDestinationStation() != null) {
                    flightDto.setDestinationStationId(flight.getDestinationStation().getId());
                    flightDto.setDestinationStationName("Station-" + flight.getDestinationStation().getId());
                }
            } catch (Exception e) {
                System.out.println("Destination station bilgisi alınamadı: " + e.getMessage());
                flightDto.setDestinationStationName("Unknown");
            }
            
            try {
                if (flight.getFlightType() != null) {
                    flightDto.setFlightTypeId(flight.getFlightType().getId());
                    flightDto.setFlightTypeName("Type-" + flight.getFlightType().getId());
                }
            } catch (Exception e) {
                System.out.println("Flight type bilgisi alınamadı: " + e.getMessage());
                flightDto.setFlightTypeName("Unknown");
            }
            
            // Native WebSocket ile gönder
            nativeWebSocketController.broadcastFlightUpdate(action, flightDto);
            
            System.out.println("=== WebSocket Mesajı Gönderildi ===");
            
        } catch (Exception e) {
            // WebSocket mesajı gönderilemezse log'la ama işlemi durdurma
            System.err.println("WebSocket mesajı gönderilemedi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // WebSocket için basit DTO sınıfı
    public static class FlightWebSocketDTO {
        private Long id;
        private String flightNumber;
        private LocalDateTime scheduledDeparture;
        private LocalDateTime scheduledArrival;
        private FlightStatus status;
        private String description;
        
        // Airline bilgileri
        private Long airlineId;
        private String airlineName;
        
        // Aircraft bilgileri
        private Long aircraftId;
        private String aircraftModel;
        
        // Station bilgileri
        private Long originStationId;
        private String originStationName;
        private Long destinationStationId;
        private String destinationStationName;
        
        // FlightType bilgileri
        private Long flightTypeId;
        private String flightTypeName;
        
        // Getter ve Setter metodları
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getFlightNumber() { return flightNumber; }
        public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
        
        public LocalDateTime getScheduledDeparture() { return scheduledDeparture; }
        public void setScheduledDeparture(LocalDateTime scheduledDeparture) { this.scheduledDeparture = scheduledDeparture; }
        
        public LocalDateTime getScheduledArrival() { return scheduledArrival; }
        public void setScheduledArrival(LocalDateTime scheduledArrival) { this.scheduledArrival = scheduledArrival; }
        
        public FlightStatus getStatus() { return status; }
        public void setStatus(FlightStatus status) { this.status = status; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Long getAirlineId() { return airlineId; }
        public void setAirlineId(Long airlineId) { this.airlineId = airlineId; }
        
        public String getAirlineName() { return airlineName; }
        public void setAirlineName(String airlineName) { this.airlineName = airlineName; }
        
        public Long getAircraftId() { return aircraftId; }
        public void setAircraftId(Long aircraftId) { this.aircraftId = aircraftId; }
        
        public String getAircraftModel() { return aircraftModel; }
        public void setAircraftModel(String aircraftModel) { this.aircraftModel = aircraftModel; }
        
        public Long getOriginStationId() { return originStationId; }
        public void setOriginStationId(Long originStationId) { this.originStationId = originStationId; }
        
        public String getOriginStationName() { return originStationName; }
        public void setOriginStationName(String originStationName) { this.originStationName = originStationName; }
        
        public Long getDestinationStationId() { return destinationStationId; }
        public void setDestinationStationId(Long destinationStationId) { this.destinationStationId = destinationStationId; }
        
        public String getDestinationStationName() { return destinationStationName; }
        public void setDestinationStationName(String destinationStationName) { this.destinationStationName = destinationStationName; }
        
        public Long getFlightTypeId() { return flightTypeId; }
        public void setFlightTypeId(Long flightTypeId) { this.flightTypeId = flightTypeId; }
        
        public String getFlightTypeName() { return flightTypeName; }
        public void setFlightTypeName(String flightTypeName) { this.flightTypeName = flightTypeName; }
    }
} 