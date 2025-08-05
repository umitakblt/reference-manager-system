package com.umitakbulut.reference_manager.mapper;

import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import com.umitakbulut.reference_manager.entity.Flight;
import com.umitakbulut.reference_manager.entity.FlightArchive;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface FlightArchiveMapper {
    FlightArchive fromFlight(Flight flight);

    FlightResponseDTO toDto(FlightArchive flightArchive);
    FlightArchive toEntity(FlightResponseDTO dto);

    @AfterMapping
    default void setArchivedAt(@MappingTarget FlightArchive archive) {
        archive.setId(null);
        archive.setArchivedAt(LocalDateTime.now());
    }
}
