package com.umitakbulut.reference_manager.mapper;

import com.umitakbulut.reference_manager.dto.request.FlightRequestDTO;
import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import com.umitakbulut.reference_manager.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AirlineMapper.class, AircraftMapper.class, StationMapper.class, FlightTypeMapper.class})
public interface FlightMapper {

    @Mapping(source = "airline.id", target = "airlineId")
    @Mapping(source = "aircraft.id", target = "aircraftId")
    @Mapping(source = "originStation.id", target = "originStationId")
    @Mapping(source = "destinationStation.id", target = "destinationStationId")
    @Mapping(source = "airline.name", target = "airlineName")
    @Mapping(source = "aircraft.model", target = "aircraftModel")
    @Mapping(source = "originStation.name", target = "originStationName")
    @Mapping(source = "destinationStation.name", target = "destinationStationName")
    @Mapping(source = "flightType.name", target = "flightTypeName")
    FlightResponseDTO toDto(Flight entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "airline", ignore = true)
    @Mapping(target = "aircraft", ignore = true)
    @Mapping(target = "originStation", ignore = true)
    @Mapping(target = "destinationStation", ignore = true)
    @Mapping(target = "flightType", ignore = true)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "description", source = "description")
    Flight toEntity(FlightRequestDTO dto);

    List<FlightResponseDTO> toDtoList(List<Flight> flights);
}