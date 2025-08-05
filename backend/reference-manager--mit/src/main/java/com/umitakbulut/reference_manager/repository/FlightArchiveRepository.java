package com.umitakbulut.reference_manager.repository;

import com.umitakbulut.reference_manager.entity.FlightArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightArchiveRepository extends JpaRepository<FlightArchive, Long> {
    Optional<FlightArchive> findByFlightNumber(String flightNumber);
}
