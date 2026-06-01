package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.dto.Holiday.HolidayRequest;
import com.example.hr_managment_system.dto.Holiday.HolidayResponse;
import com.example.hr_managment_system.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/holiday")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HolidayResponse createHoliday(@RequestBody HolidayRequest request) {
        return holidayService.createHoliday(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<HolidayResponse> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    @GetMapping("/current-month")
    @ResponseStatus(HttpStatus.OK)
    public List<HolidayResponse> getHolidaysForCurrentMonth() {
        return holidayService.getHolidaysForCurrentMonth();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHoliday(@PathVariable String id) {
        holidayService.deleteHoliday(id);
    }
}
