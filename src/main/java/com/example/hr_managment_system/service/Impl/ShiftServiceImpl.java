package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.Shift;
import com.example.hr_managment_system.dto.shift.ShiftRequest;
import com.example.hr_managment_system.dto.shift.ShiftResponse;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.repository.ShiftRepository;
import com.example.hr_managment_system.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public ShiftResponse createShift(ShiftRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift name is required.");
        }
        if (request.startTime() == null || request.endTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time and end time are required.");
        }

        Shift shift = new Shift();
        shift.setName(request.name().trim());
        shift.setStartTime(request.startTime());
        shift.setEndTime(request.endTime());
        shift.setGracePeriodMinutes(request.gracePeriodMinutes() == null ? 0 : request.gracePeriodMinutes());
        shift.setLatePenaltyAmount(request.latePenaltyAmount() == null ? 0.0 : request.latePenaltyAmount());

        Shift saved = shiftRepository.save(shift);
        return mapToResponse(saved);
    }

    @Override
    public List<ShiftResponse> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void assignShiftToEmployee(String employeeId, String shiftId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found."));

        if (shiftId == null || shiftId.trim().isEmpty()) {
            employee.setShift(null);
        } else {
            Shift shift = shiftRepository.findById(shiftId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found."));
            employee.setShift(shift);
        }

        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public ShiftResponse updateShift(String shiftId, ShiftRequest request) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found."));

        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift name is required.");
        }
        if (request.startTime() == null || request.endTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time and end time are required.");
        }

        // Check unique shift name excluding current shiftId
        shiftRepository.findAll().stream()
                .filter(s -> s.getName().equalsIgnoreCase(request.name().trim()) && !s.getShiftId().equals(shiftId))
                .findFirst()
                .ifPresent(s -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift name already exists.");
                });

        shift.setName(request.name().trim());
        shift.setStartTime(request.startTime());
        shift.setEndTime(request.endTime());
        shift.setGracePeriodMinutes(request.gracePeriodMinutes() == null ? 0 : request.gracePeriodMinutes());
        shift.setLatePenaltyAmount(request.latePenaltyAmount() == null ? 0.0 : request.latePenaltyAmount());

        Shift saved = shiftRepository.save(shift);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteShift(String shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found."));

        employeeRepository.detachShiftFromEmployees(shiftId);
        shiftRepository.delete(shift);
    }

    private ShiftResponse mapToResponse(Shift shift) {
        return new ShiftResponse(
                shift.getShiftId(),
                shift.getName(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getGracePeriodMinutes(),
                shift.getLatePenaltyAmount()
        );
    }
}
