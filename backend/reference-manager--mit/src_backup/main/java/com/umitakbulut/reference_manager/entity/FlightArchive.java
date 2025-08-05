package com.umitakbulut.reference_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight_archive")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FlightArchive implements Serializable {

    @Serial
    private static final long serialVersionUID = 8438439438L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    @Column(name = "airline_id")
    private Long airlineId;

    @Column(name = "aircraft_id")
    private Long aircraftId;

    @Column(name = "origin_station_id")
    private Long originStationId;

    @Column(name = "destination_station_id")
    private Long destinationStationId;

    @Column(name = "flight_type_id")
    private Long flightTypeId;

    @Column(name = "airline_name", nullable = false)
    private String airlineName;

    @Column(name = "aircraft_model", nullable = false)
    private String aircraftModel;

    @Column(name = "origin_station_code", nullable = false)
    private String originStationCode;

    @Column(name = "destination_station_code", nullable = false)
    private String destinationStationCode;

    @Column(name = "scheduled_departure", nullable = false)
    private LocalDateTime scheduledDeparture;

    @Column(name = "scheduled_arrival", nullable = false)
    private LocalDateTime scheduledArrival;

    @Column(name = "flight_type_name", nullable = false)
    private String flightTypeName;

    @Column(name = "archived_at", nullable = false)
    private LocalDateTime archivedAt;

    @Column(name = "status")
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
