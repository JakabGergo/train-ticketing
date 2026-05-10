package com.trainticket.api.dto;

import lombok.*;

@Data
@Builder
public class StationCreateDTO {
    private String name;
    private String city;
}