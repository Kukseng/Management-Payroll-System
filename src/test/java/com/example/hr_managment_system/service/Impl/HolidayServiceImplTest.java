package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Holiday;
import com.example.hr_managment_system.dto.Holiday.HolidayRequest;
import com.example.hr_managment_system.dto.Holiday.HolidayResponse;
import com.example.hr_managment_system.repository.HolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayServiceImplTest {

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayServiceImpl holidayService;

    private Holiday testHoliday;

    @BeforeEach
    void setUp() {
        testHoliday = new Holiday();
        testHoliday.setHolidayId("hol-001");
        testHoliday.setHolidayName("Freedom Day");
        testHoliday.setHolidayDate(LocalDate.of(2026, 6, 15));
        testHoliday.setDescription("Celebration of freedom");
    }

    @Test
    void createHoliday_ShouldSaveAndReturnResponse_WhenDateIsNotTaken() {
        HolidayRequest request = new HolidayRequest("Freedom Day", LocalDate.of(2026, 6, 15), "Celebration of freedom");
        when(holidayRepository.existsByHolidayDate(request.holidayDate())).thenReturn(false);
        when(holidayRepository.save(any(Holiday.class))).thenReturn(testHoliday);

        HolidayResponse response = holidayService.createHoliday(request);

        assertNotNull(response);
        assertEquals("Freedom Day", response.holidayName());
        assertEquals(LocalDate.of(2026, 6, 15), response.holidayDate());
        verify(holidayRepository, times(1)).save(any(Holiday.class));
    }

    @Test
    void createHoliday_ShouldThrowException_WhenDateIsAlreadyTaken() {
        HolidayRequest request = new HolidayRequest("Another Holiday", LocalDate.of(2026, 6, 15), "Desc");
        when(holidayRepository.existsByHolidayDate(request.holidayDate())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> holidayService.createHoliday(request));
        verify(holidayRepository, never()).save(any(Holiday.class));
    }

    @Test
    void getAllHolidays_ShouldReturnSortedList() {
        Holiday h1 = new Holiday();
        h1.setHolidayDate(LocalDate.of(2026, 6, 15));
        Holiday h2 = new Holiday();
        h2.setHolidayDate(LocalDate.of(2026, 7, 4));

        when(holidayRepository.findAll()).thenReturn(List.of(h1, h2));

        List<HolidayResponse> result = holidayService.getAllHolidays();

        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2026, 7, 4), result.get(0).holidayDate()); // descending order (latest first)
    }

    @Test
    void deleteHoliday_ShouldCallDelete_WhenHolidayExists() {
        when(holidayRepository.findById("hol-001")).thenReturn(Optional.of(testHoliday));
        doNothing().when(holidayRepository).delete(testHoliday);

        holidayService.deleteHoliday("hol-001");

        verify(holidayRepository, times(1)).delete(testHoliday);
    }

    @Test
    void deleteHoliday_ShouldThrowException_WhenHolidayDoesNotExist() {
        when(holidayRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> holidayService.deleteHoliday("non-existent"));
        verify(holidayRepository, never()).delete(any(Holiday.class));
    }
}
