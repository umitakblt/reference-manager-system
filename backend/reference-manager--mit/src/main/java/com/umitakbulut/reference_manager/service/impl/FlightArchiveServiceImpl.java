package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.entity.Flight;
import com.umitakbulut.reference_manager.entity.FlightArchive;
import com.umitakbulut.reference_manager.mapper.FlightArchiveMapper;
import com.umitakbulut.reference_manager.repository.FlightArchiveRepository;
import com.umitakbulut.reference_manager.service.FlightArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightArchiveServiceImpl implements FlightArchiveService {

    private final FlightArchiveRepository repository;

    @Qualifier("flightArchiveMapper")
    private final FlightArchiveMapper flightArchiveMapper;

    @Override
    public void saveFromFlight(Flight flight) {
        try {
            Optional<FlightArchive> existingArchive = repository.findByFlightNumber(flight.getFlightNumber());
            if (existingArchive.isPresent()) {
                System.out.println("Bu uçuş zaten arşivlenmiş: " + flight.getFlightNumber());
                return;
            }
            
            FlightArchive archive = new FlightArchive();
            archive.setFlightNumber(flight.getFlightNumber());
            
            archive.setAirlineId(flight.getAirline() != null ? flight.getAirline().getId() : null);
            archive.setAircraftId(flight.getAircraft() != null ? flight.getAircraft().getId() : null);
            archive.setOriginStationId(flight.getOriginStation() != null ? flight.getOriginStation().getId() : null);
            archive.setDestinationStationId(flight.getDestinationStation() != null ? flight.getDestinationStation().getId() : null);
            archive.setFlightTypeId(flight.getFlightType() != null ? flight.getFlightType().getId() : null);
            
            String airlineName = "Unknown";
            try {
                if (flight.getAirline() != null) {
                    airlineName = flight.getAirline().getName();
                }
            } catch (Exception e) {
                airlineName = "Airline-" + (flight.getAirline() != null ? flight.getAirline().getId() : "Unknown");
            }
            archive.setAirlineName(airlineName);
            
            String aircraftModel = "Unknown";
            try {
                if (flight.getAircraft() != null) {
                    aircraftModel = flight.getAircraft().getModel();
                }
            } catch (Exception e) {
                aircraftModel = "Aircraft-" + (flight.getAircraft() != null ? flight.getAircraft().getId() : "Unknown");
            }
            archive.setAircraftModel(aircraftModel);
            
            String originCode = "Unknown";
            try {
                if (flight.getOriginStation() != null) {
                    originCode = flight.getOriginStation().getCode();
                }
            } catch (Exception e) {
                originCode = "Station-" + (flight.getOriginStation() != null ? flight.getOriginStation().getId() : "Unknown");
            }
            archive.setOriginStationCode(originCode);
            
            String destCode = "Unknown";
            try {
                if (flight.getDestinationStation() != null) {
                    destCode = flight.getDestinationStation().getCode();
                }
            } catch (Exception e) {
                destCode = "Station-" + (flight.getDestinationStation() != null ? flight.getDestinationStation().getId() : "Unknown");
            }
            archive.setDestinationStationCode(destCode);
            
            archive.setScheduledDeparture(flight.getScheduledDeparture());
            archive.setScheduledArrival(flight.getScheduledArrival());
            
            String flightTypeName = "Unknown";
            try {
                if (flight.getFlightType() != null) {
                    flightTypeName = flight.getFlightType().getName();
                }
            } catch (Exception e) {
                flightTypeName = "Type-" + (flight.getFlightType() != null ? flight.getFlightType().getId() : "Unknown");
            }
            archive.setFlightTypeName(flightTypeName);
            
            archive.setStatus(flight.getStatus() != null ? flight.getStatus().name() : "UNKNOWN");
            archive.setDescription(flight.getDescription());
            
            archive.setArchivedAt(LocalDateTime.now());
            
            repository.save(archive);
            System.out.println("Arşivleme başarılı: " + flight.getFlightNumber());
        } catch (Exception e) {
            System.err.println("Arşivleme hatası: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<FlightArchive> getAllArchivedFlights() {
        return repository.findAll();
    }

    @Override
    public void updateArchivedAtForExistingRecords() {
        List<FlightArchive> archives = repository.findAll();
        for (FlightArchive archive : archives) {
            if (archive.getArchivedAt() == null) {
                archive.setArchivedAt(LocalDateTime.now());
                repository.save(archive);
            }
        }
    }

    @Override
    public void deleteArchivedFlight(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAllArchivedFlights() {
        repository.deleteAll();
    }
}
