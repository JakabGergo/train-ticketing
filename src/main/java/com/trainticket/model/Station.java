package com.trainticket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Station extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String city;
}