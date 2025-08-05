package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.dto.request.RouteRequestDTO;
import com.umitakbulut.reference_manager.dto.response.RouteResponseDTO;
import com.umitakbulut.reference_manager.entity.Route;
import com.umitakbulut.reference_manager.exception.NotFoundException;
import com.umitakbulut.reference_manager.kafka.KafkaProducer;
import com.umitakbulut.reference_manager.mapper.RouteMapper;
import com.umitakbulut.reference_manager.repository.RouteRepository;
import com.umitakbulut.reference_manager.repository.StationRepository;
import com.umitakbulut.reference_manager.entity.Station;
import com.umitakbulut.reference_manager.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private static final Logger log = LoggerFactory.getLogger(RouteServiceImpl.class);
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final KafkaProducer kafkaProducer;
    private final StationRepository stationRepository;

    @Override
    public RouteResponseDTO save(RouteRequestDTO requestDTO) {
        Station origin = stationRepository.findById(requestDTO.getOriginStationId())
                .orElseThrow(() -> new NotFoundException("Origin station not found"));
        Station destination = stationRepository.findById(requestDTO.getDestinationStationId())
                .orElseThrow(() -> new NotFoundException("Destination station not found"));

        log.info("Gelen distance: {}", requestDTO.getDistance());

        Route route = new Route();
        route.setOriginStation(origin);
        route.setDestinationStation(destination);
        route.setDistance(requestDTO.getDistance());

        Route saved = routeRepository.save(route);

        kafkaProducer.sendRoute(saved);

        return routeMapper.toDto(saved);
    }

    @Override
    public List<RouteResponseDTO> getAll() {
        return routeRepository.findAll()
                .stream()
                .map(routeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RouteResponseDTO getById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route not found"));
        return routeMapper.toDto(route);
    }

    @Override
    public RouteResponseDTO update(Long id, RouteRequestDTO requestDTO) {
        Route existing = routeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route not found"));

        Route updated = routeMapper.toEntity(requestDTO);
        updated.setId(existing.getId());

        Route saved = routeRepository.save(updated);
        return routeMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        routeRepository.deleteById(id);
    }

    public RouteResponseDTO saveFromKafka(RouteRequestDTO requestDTO) {
        Station origin = stationRepository.findById(requestDTO.getOriginStationId())
                .orElseThrow(() -> new NotFoundException("Origin station not found"));
        Station destination = stationRepository.findById(requestDTO.getDestinationStationId())
                .orElseThrow(() -> new NotFoundException("Destination station not found"));

        log.info("[KAFKA] Gelen distance: {}", requestDTO.getDistance());

        Route route = new Route();
        route.setOriginStation(origin);
        route.setDestinationStation(destination);
        route.setDistance(requestDTO.getDistance());

        Route saved = routeRepository.save(route);
        return routeMapper.toDto(saved);
    }
}