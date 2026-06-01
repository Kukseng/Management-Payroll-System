package com.example.hr_managment_system.dto.Holiday;

import java.time.LocalDate;

public record HolidayRequest(
        String holidayName,
        LocalDate holidayDate,
        String description
) {}
