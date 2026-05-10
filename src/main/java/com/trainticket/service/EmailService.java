package com.trainticket.service;

import com.trainticket.model.Booking;
import com.trainticket.model.Train;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public void sendBookingConfirmation(String toEmail, Booking booking) {
        String subject = "Booking Confirmed – Train " + booking.getTrain().getTrainNumber();

        String body = String.format("""
                Dear %s,

                Your booking has been confirmed!

                Booking Reference: #%d
                Train: %s
                From: %s
                To: %s
                Seats: %d

                Thank you for choosing our service.
                """,
                booking.getUser().getName(),
                booking.getId(),
                booking.getTrain().getTrainNumber(),
                booking.getFromStation().getName(),
                booking.getToStation().getName(),
                booking.getNumberOfSeats()
        );

        // Instead of sending a real email, we log it to the console
        log.info("=== EMAIL SENDING ===");
        log.info("To: {}", toEmail);
        log.info("Subject: Train Booking Confirmation");
        log.info("Body:\n{}", body);
        log.info("================================");

        sendEmail(booking.getUser().getEmail(), subject, body);
    }

    public void sendDelayNotification(Booking booking, int delayMinutes) {
        Train train = booking.getTrain();
        LocalTime original = LocalTime.from(train.getDepartureTime());
        LocalTime delayed = original.plusMinutes(delayMinutes);

        String subject = "⚠ Delay Notice – Train " + train.getTrainNumber();
        String body = String.format("""
                Dear %s,

                We regret to inform you that train %s is running %d minute(s) late.

                Booking Reference: #%d
                Original Departure: %s
                New Departure: %s (approx.)

                We apologise for any inconvenience.
                """,
                booking.getUser().getName(),
                train.getTrainNumber(),
                delayMinutes,
                booking.getId(),
                original.format(TIME_FMT),
                delayed.format(TIME_FMT)
        );

        log.info("=== EMAIL SENDING ===");
        log.info("To: {}", booking.getUser().getEmail());
        log.info("Subject: Train Booking Confirmation");
        log.info("Body:\n{}", body);
        log.info("================================");

        sendEmail(booking.getUser().getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (!mailEnabled) {
            log.info("[MAIL DISABLED] Would send to={} subject={}", to, subject);
            log.debug("[MAIL BODY]\n{}", body);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            log.info("Mail sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send mail to {}: {}", to, e.getMessage());
        }
    }
}