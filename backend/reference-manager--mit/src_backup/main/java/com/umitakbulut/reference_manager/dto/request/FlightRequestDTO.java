package com.umitakbulut.reference_manager.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.umitakbulut.reference_manager.entity.FlightStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FlightRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6383580496428756157L;

    private String flightNumber;
    private Long airlineId;
    private Long aircraftId;
    private Long originStationId;
    private Long destinationStationId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledDeparture;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledArrival;
    
    private Long flightTypeId;
    private FlightStatus status = FlightStatus.ON_TIME;
    private String description;
}
