package com.trainticket.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JourneyLegDTO {
    private Long trainId;
    private String fromStationName;
    private String toStationName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}
