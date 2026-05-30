package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Leave.LeaveBalanceResponse;
import com.example.hr_managment_system.dto.Leave.LeaveCreateRequest;
import com.example.hr_managment_system.dto.Leave.LeaveResponse;

import java.util.List;

public interface LeaveService {
    LeaveResponse createLeaveRequest(LeaveCreateRequest request);

    List<LeaveResponse> getMyLeaveRequests(String principal);

    List<LeaveResponse> getPendingLeaveRequests();

    LeaveResponse approveLeave(String leaveId, String approverPrincipal);

    LeaveResponse rejectLeave(String leaveId, String approverPrincipal);

    LeaveBalanceResponse getLeaveBalance(String employeeId);

    List<LeaveResponse> getAllLeaveRequests();
}
