package com.trainticket.api.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCreateDTO {
    private String name;
    private String description;
    private List<RouteStationDTO> stations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteStationDTO {
        private Long stationId;
        private int stopOrder;
        private int minutesFromOrigin;
    }
}