package com.trainticket.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Component
@EqualsAndHashCode(callSuper = true)
@Table(name = "routes")
public class Route extends BaseEntity{
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // TODO connection to stations
}
