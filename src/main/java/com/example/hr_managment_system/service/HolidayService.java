package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Holiday.HolidayRequest;
import com.example.hr_managment_system.dto.Holiday.HolidayResponse;

import java.util.List;

public interface HolidayService {
    HolidayResponse createHoliday(HolidayRequest request);
    List<HolidayResponse> getAllHolidays();
    List<HolidayResponse> getHolidaysForCurrentMonth();
    void deleteHoliday(String id);
}
