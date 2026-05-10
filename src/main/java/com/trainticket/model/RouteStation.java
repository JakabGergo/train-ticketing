package com.trainticket.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Table(name = "route_stations")
@Builder
public class RouteStation extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    @JsonBackReference("route-routestation")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private int stopOrder;

    @Column(nullable = false)
    private int minutesFromOrigin;
}
