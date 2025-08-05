package com.umitakbulut.reference_manager.repository;

import com.umitakbulut.reference_manager.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f.flightNumber FROM Flight f WHERE f.flightNumber IN :flightNumbers")
    List<String> findExistingFlightNumbers(@Param("flightNumbers") List<String> flightNumbers);
    
    @Query("SELECT DISTINCT f FROM Flight f " +
           "LEFT JOIN FETCH f.airline " +
           "LEFT JOIN FETCH f.aircraft " +
           "LEFT JOIN FETCH f.originStation " +
           "LEFT JOIN FETCH f.destinationStation " +
           "LEFT JOIN FETCH f.flightType " +
           "ORDER BY f.scheduledDeparture ASC")
    List<Flight> findAllWithRelations();
}
