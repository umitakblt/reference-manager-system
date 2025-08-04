package com.umitakbulut.reference_manager.dto.response;

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
public class FlightResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7194295688766988659L;

    private Long id;
    private String flightNumber;
    private Long airlineId;
    private Long aircraftId;
    private Long originStationId;
    private Long destinationStationId;
    
    // İlişkili entity'ler - sadece gerekli bilgiler
    private String airlineName;
    private String aircraftModel;
    private String originStationName;
    private String destinationStationName;
    private String flightTypeName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledDeparture;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledArrival;
    
    private FlightStatus status;
    private String description;
}
