package com.umitakbulut.reference_manager.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight")
@Getter
@Setter
@ToString(exclude = {"airline", "aircraft", "originStation", "destinationStation", "flightType"})
@NoArgsConstructor
@AllArgsConstructor
public class Flight implements Serializable {

    @Serial
    private static final long serialVersionUID = -6360679211095651595L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "airline_id", nullable = false, unique = true)
    private Airline airline;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aircraft_id", nullable = false, unique = true)
    private Aircraft aircraft;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origin_station_id", nullable = false, unique = true)
    private Station originStation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_station_id", nullable = false, unique = true)
    private Station destinationStation;

    @Column(name = "scheduled_departure", nullable = false, unique = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledDeparture;

    @Column(name = "scheduled_arrival", nullable = false, unique = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledArrival;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "flight_type_id", nullable = false, unique = true)
    private FlightType flightType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.ON_TIME;

    @Column(name = "description")
    private String description;
}
