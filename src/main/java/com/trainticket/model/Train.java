package com.trainticket.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trains")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Train extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String trainNumber;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int delayMinutes = 0;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
}
