package com.trainticket.api.dto;

import com.trainticket.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private String userName;      // Must match Mapper source "user.name"
    private String trainName;     // Must match Mapper target
    private String fromStationName;
    private String toStationName;
    private int numberOfSeats;
    private BookingStatus status;
    private LocalDateTime createdAt;
}