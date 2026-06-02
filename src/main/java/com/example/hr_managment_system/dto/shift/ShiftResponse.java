package com.example.hr_managment_system.dto.shift;

import java.time.LocalTime;

public record ShiftResponse(
        String shiftId,
        String name,
        LocalTime startTime,
        LocalTime endTime,
        Integer gracePeriodMinutes,
        Double latePenaltyAmount
) {
}
