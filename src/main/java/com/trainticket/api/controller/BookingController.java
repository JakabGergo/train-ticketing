package com.trainticket.api.controller;

import com.trainticket.api.dto.BookingRequestDTO;
import com.trainticket.api.dto.BookingResponseDTO;
import com.trainticket.api.mapper.BookingMapper;
import com.trainticket.model.Booking;
import com.trainticket.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    /**
     * Requirement (a): Ability to book one or multiple tickets[cite: 6].
     * This handles overbooking checks and email notifications internally[cite: 7, 8, 9, 10].
     */
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO request) {

        Booking booking = bookingService.bookingTickets(
                request.getUserId(),
                request.getTrainId(),
                request.getFromStationId(),
                request.getToStationId(),
                request.getNumberOfSeats()
        );

        return ResponseEntity.ok(bookingMapper.modelToResponseDto(booking));
    }

    @GetMapping
    public ResponseEntity<Collection<BookingResponseDTO>> getAllBookings(){
        return ResponseEntity.ok(bookingMapper.modelsToResponseDtos(bookingService.findAll()));
    }
}
