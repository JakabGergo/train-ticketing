package com.trainticket.api.dto;

import lombok.*;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainCreateDTO {
    private String trainNumber;
    private LocalTime departureTime;
    private int totalSeats;
    private Long routeId;
}