package com.trainticket.service;

import com.trainticket.model.*;
import com.trainticket.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final TrainRepository trainRepository;
    private final RouteStationRepository routeStationRepository;

    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public BookingService(BookingRepository bookingRepository, TrainRepository trainRepository, RouteStationRepository routeStationRepository, StationRepository stationRepository, UserRepository userRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.trainRepository = trainRepository;
        this.routeStationRepository = routeStationRepository;
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking bookingTickets(Long userId, Long trainId, Long fromStationId, Long toStationId, int numberOfSeats) {
        // Fetch entities by ID inside the service
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new IllegalArgumentException("Train not found"));
        Station fromStation = stationRepository.findById(fromStationId)
                .orElseThrow(() -> new IllegalArgumentException("Start station not found"));
        Station toStation = stationRepository.findById(toStationId)
                .orElseThrow(() -> new IllegalArgumentException("End station not found"));

        // 1. Get the stop orders for the requested segment
        int startOrder = getStopOrder(train.getRoute().getId(), fromStation.getId());
        int endOrder = getStopOrder(train.getRoute().getId(), toStation.getId());

        if (startOrder >= endOrder) {
            throw new IllegalArgumentException("Invalid route segment: Destination must be after origin.");
        }

        // 2. Prevent overbooking
        validateSeatAvailability(train, startOrder, endOrder, numberOfSeats);

        // 3. Create and save the booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrain(train);
        booking.setFromStation(fromStation);
        booking.setToStation(toStation);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setStatus(BookingStatus.CONFIRMED); // Set default status
        booking.setCreatedAt(LocalDateTime.now()); // Set timestamp

        Booking savedBooking = bookingRepository.save(booking);

        // 4. Send Confirmation Email (mocked log)
        emailService.sendBookingConfirmation(user.getEmail(), savedBooking);

        return savedBooking;
    }

    private int getStopOrder(Long routeId, Long stationId) {
        return routeStationRepository.findByRouteIdAndStationId(routeId, stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station not on this route"))
                .getStopOrder();
    }

    private void validateSeatAvailability(Train train, int newStartOrder, int newEndOrder, int newTickets) {
        List<Booking> existingBookings = bookingRepository.findByTrainId(train.getId());

        // Check availability segment by segment (stop by stop)
        for (int currentSegment = newStartOrder; currentSegment < newEndOrder; currentSegment++) {
            int occupiedSeatsInThisSegment = 0;

            for (Booking existing : existingBookings) {
                int existingStart = getStopOrder(train.getRoute().getId(), existing.getFromStation().getId());
                int existingEnd = getStopOrder(train.getRoute().getId(), existing.getToStation().getId());

                // Does the existing booking overlap with the current segment?
                if (existingStart <= currentSegment && existingEnd > currentSegment) {
                    occupiedSeatsInThisSegment += existing.getNumberOfSeats();
                }
            }

            if (occupiedSeatsInThisSegment + newTickets > train.getTotalSeats()) {
                throw new IllegalStateException("Not enough seats available for the selected segment.");
            }
        }
    }
}
