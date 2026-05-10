package com.trainticket.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Component
@EqualsAndHashCode(callSuper = true)
@Table(name = "routes")
@Builder
public class Route extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    @OrderBy("stopOrder ASC")
    @JsonManagedReference("route-routestation")
    private List<RouteStation> routeStations;
}
