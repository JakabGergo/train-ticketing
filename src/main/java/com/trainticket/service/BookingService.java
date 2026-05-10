package com.trainticket.service;

import com.trainticket.model.Booking;
import com.trainticket.model.Station;
import com.trainticket.model.Train;
import com.trainticket.model.User;
import com.trainticket.repository.BookingRepository;
import com.trainticket.repository.RouteStationRepository;
import com.trainticket.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final TrainRepository trainRepository;
    private final RouteStationRepository routeStationRepository;

    private final EmailService emailService;

    public BookingService(BookingRepository bookingRepository, TrainRepository trainRepository, RouteStationRepository routeStationRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.trainRepository = trainRepository;
        this.routeStationRepository = routeStationRepository;
        this.emailService = emailService;
    }

    public Booking bookingTickets(User user, Long trainId, Station fromStation, Station toStation, int numberOfSeats) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new IllegalArgumentException("Train not found"));

        // 1. Get the stop orders for the requested segment
        int startOrder = getStopOrder(train.getRoute().getId(), fromStation.getId());
        int endOrder = getStopOrder(train.getRoute().getId(), toStation.getId());

        if (startOrder >= endOrder) {
            throw new IllegalArgumentException("Invalid route segment selected.");
        }

        // 2. Prevent overbooking by checking segment availability
        validateSeatAvailability(train, startOrder, endOrder, numberOfSeats);

        // 3. Create and save the booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrain(train);
        booking.setFromStation(fromStation);
        booking.setToStation(toStation);
        booking.setNumberOfSeats(numberOfSeats);

        Booking savedBooking = bookingRepository.save(booking);

        // 4. Send Confirmation Email
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
