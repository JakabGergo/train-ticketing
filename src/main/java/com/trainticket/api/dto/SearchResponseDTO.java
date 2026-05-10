package com.trainticket.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResponseDTO {
    private List<JourneyLegDTO> legs;
    private boolean isDirect;
    private long totalDurationMinutes;
}
