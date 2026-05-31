package com.example.hr_managment_system.dto.shift;

import java.time.LocalTime;

public record ShiftRequest(
        String name,
        LocalTime startTime,
        LocalTime endTime,
        Integer gracePeriodMinutes
) {
}
