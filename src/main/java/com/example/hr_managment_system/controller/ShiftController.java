package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.dto.shift.ShiftRequest;
import com.example.hr_managment_system.dto.shift.ShiftResponse;
import com.example.hr_managment_system.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ShiftResponse createShift(@RequestBody ShiftRequest request) {
        return shiftService.createShift(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ShiftResponse> getAllShifts() {
        return shiftService.getAllShifts();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/assign")
    public String assignShiftToEmployee(@RequestParam String employeeId,
                                        @RequestParam(required = false) String shiftId) {
        shiftService.assignShiftToEmployee(employeeId, shiftId);
        return "Shift assigned successfully.";
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public ShiftResponse updateShift(@PathVariable String id, @RequestBody ShiftRequest request) {
        return shiftService.updateShift(id, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteShift(@PathVariable String id) {
        shiftService.deleteShift(id);
    }
}
