package com.trainticket.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelayDTO {
    private int delayMinutes;
}