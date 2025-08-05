package com.umitakbulut.reference_manager.dto.websocket;

import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FlightUpdateMessage {
    private String action;
    private FlightResponseDTO flight;
    private String message;
    private LocalDateTime timestamp;
} 