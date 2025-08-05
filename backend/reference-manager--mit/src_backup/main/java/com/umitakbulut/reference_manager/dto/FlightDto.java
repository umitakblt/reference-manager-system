package com.umitakbulut.reference_manager.dto;

import lombok.Data;

@Data
public class FlightDto {
    private Long id;
    private String from;
    private String to;
    private String time;
    private String aircraft;
    private String status;
    private String airline;
} 