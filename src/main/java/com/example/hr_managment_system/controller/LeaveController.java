package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.dto.Leave.LeaveBalanceResponse;
import com.example.hr_managment_system.dto.Leave.LeaveCreateRequest;
import com.example.hr_managment_system.dto.Leave.LeaveResponse;
import com.example.hr_managment_system.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveResponse create(@RequestBody LeaveCreateRequest request) {
        return leaveService.createLeaveRequest(request);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public List<LeaveResponse> getMyLeaves(Authentication authentication) {
        return leaveService.getMyLeaveRequests(authentication.getName());
    }

    @GetMapping("/pending")
    @ResponseStatus(HttpStatus.OK)
    public List<LeaveResponse> getPendingLeaves() {
        return leaveService.getPendingLeaveRequests();
    }

    @PatchMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.OK)
    public LeaveResponse approve(@PathVariable String id, Authentication authentication) {
        return leaveService.approveLeave(id, authentication.getName());
    }

    @PatchMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public LeaveResponse reject(@PathVariable String id, Authentication authentication) {
        return leaveService.rejectLeave(id, authentication.getName());
    }

    @GetMapping("/balance/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public LeaveBalanceResponse leaveBalance(@PathVariable String employeeId) {
        return leaveService.getLeaveBalance(employeeId);
    }
}
