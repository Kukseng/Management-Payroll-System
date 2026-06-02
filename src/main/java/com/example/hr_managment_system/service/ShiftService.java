package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.shift.ShiftRequest;
import com.example.hr_managment_system.dto.shift.ShiftResponse;

import java.util.List;

public interface ShiftService {
    ShiftResponse createShift(ShiftRequest request);
    List<ShiftResponse> getAllShifts();
    void assignShiftToEmployee(String employeeId, String shiftId);
    ShiftResponse updateShift(String shiftId, ShiftRequest request);
    void deleteShift(String shiftId);
}
