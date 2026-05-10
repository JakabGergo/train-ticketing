package com.trainticket.service;

import com.trainticket.model.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    public void sendBookingConfirmation(String toEmail, Booking booking) {
        String emailBody = "Dear " + booking.getUser().getName() + ",\n\n" +
                "Your booking is confirmed!\n" +
                "Train ID: " + booking.getTrain().getId() + "\n" +
                "From: " + booking.getFromStation().getName() + "\n" +
                "To: " + booking.getToStation().getName() + "\n" +
                "Tickets: " + booking.getNumberOfSeats() + "\n\n" +
                "Thank you for traveling with us.";

        // Instead of sending a real email, we log it to the console
        log.info("=== SIMULATING EMAIL SENDING ===");
        log.info("To: {}", toEmail);
        log.info("Subject: Train Booking Confirmation");
        log.info("Body:\n{}", emailBody);
        log.info("================================");
    }
}