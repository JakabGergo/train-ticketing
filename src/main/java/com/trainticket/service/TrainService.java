package com.trainticket.service;

import com.trainticket.api.dto.BookingResponseDTO;
import com.trainticket.api.dto.DelayDTO;
import com.trainticket.api.dto.TrainCreateDTO;
import com.trainticket.model.Booking;
import com.trainticket.model.BookingStatus;
import com.trainticket.model.Route;
import com.trainticket.model.Train;
import com.trainticket.repository.BookingRepository;
import com.trainticket.repository.RouteRepository;
import com.trainticket.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainService {
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public Train createTrain(TrainCreateDTO dto) {
        Route route = routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        Train train = Train.builder()
                .trainNumber(dto.getTrainNumber())
                .departureTime(dto.getDepartureTime())
                .totalSeats(dto.getTotalSeats())
                .delayMinutes(0)
                .route(route)
                .build();
        return trainRepository.save(train);
    }

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public Train updateTrain(Long id, TrainCreateDTO dto) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));
        Route route = routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        train.setTrainNumber(dto.getTrainNumber());
        train.setDepartureTime(dto.getDepartureTime());
        train.setTotalSeats(dto.getTotalSeats());
        train.setRoute(route);
        return trainRepository.save(train);
    }

    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
    }

    @Transactional
    public Train setDelay(Long trainId, DelayDTO delayDTO) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        train.setDelayMinutes(delayDTO.getDelayMinutes());
        trainRepository.save(train);

        // Notify all confirmed passengers
        List<Booking> confirmedBookings = bookingRepository
                .findByTrainIdAndStatus(trainId, BookingStatus.CONFIRMED);

        for (Booking booking : confirmedBookings) {
            emailService.sendDelayNotification(booking, delayDTO.getDelayMinutes());
        }

        return train;
    }

    public List<BookingResponseDTO> getTrainBookings(Long trainId) {
        return bookingRepository.findByTrainId(trainId).stream()
                .map(b -> BookingResponseDTO.builder()
                        .id(b.getId())
                        .userName(b.getUser().getName())
                        .userEmail(b.getUser().getEmail())
                        .trainName(b.getTrain().getTrainNumber())
                        .fromStationName(b.getFromStation().getName())
                        .toStationName(b.getToStation().getName())
                        .numberOfSeats(b.getNumberOfSeats())
                        .status(b.getStatus())
                        .createdAt(b.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}