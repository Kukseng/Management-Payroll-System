package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Holiday;
import com.example.hr_managment_system.dto.Holiday.HolidayRequest;
import com.example.hr_managment_system.dto.Holiday.HolidayResponse;
import com.example.hr_managment_system.repository.HolidayRepository;
import com.example.hr_managment_system.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    @Override
    public HolidayResponse createHoliday(HolidayRequest request) {
        if (holidayRepository.existsByHolidayDate(request.holidayDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A holiday is already scheduled on this date");
        }
        Holiday holiday = new Holiday();
        holiday.setHolidayName(request.holidayName());
        holiday.setHolidayDate(request.holidayDate());
        holiday.setDescription(request.description());

        Holiday saved = holidayRepository.save(holiday);
        return mapToResponse(saved);
    }

    @Override
    public List<HolidayResponse> getAllHolidays() {
        return holidayRepository.findAll().stream()
                .map(this::mapToResponse)
                .sorted((h1, h2) -> h2.holidayDate().compareTo(h1.holidayDate())) // Show latest holidays on top
                .toList();
    }

    @Override
    public List<HolidayResponse> getHolidaysForCurrentMonth() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = today.with(TemporalAdjusters.lastDayOfMonth());
        return holidayRepository.findByHolidayDateBetween(start, end).stream()
                .map(this::mapToResponse)
                .sorted((h1, h2) -> h1.holidayDate().compareTo(h2.holidayDate())) // Show earliest first within the month
                .toList();
    }

    @Override
    public void deleteHoliday(String id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Holiday not found"));
        holidayRepository.delete(holiday);
    }

    private HolidayResponse mapToResponse(Holiday holiday) {
        return new HolidayResponse(
                holiday.getHolidayId(),
                holiday.getHolidayName(),
                holiday.getHolidayDate(),
                holiday.getDescription()
        );
    }
}
