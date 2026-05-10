package com.trainticket.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Booking extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne
    @JoinColumn(name = "from_station_id", nullable = false)
    private Station fromStation;

    @ManyToOne
    @JoinColumn(name = "to_station_id", nullable = false)
    private Station toStation;

    @Column(nullable = false)
    private int numberOfSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
