package com.example.hr_managment_system.mapper;

import com.example.hr_managment_system.domain.Payroll;
import com.example.hr_managment_system.dto.Payroll.PayrollRequest;
import com.example.hr_managment_system.dto.Payroll.PayrollResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PayrollMapper {

    @Mapping(source = "employee.employeeId", target = "employeeId")
    @Mapping(source = "employee.firstName", target = "employeeFirstName")
    @Mapping(source = "employee.lastName", target = "employeeLastName")
    PayrollResponse PayrolltoPayrollResponse(Payroll payroll);

    Payroll PayrollRequesttoPayroll(PayrollRequest payrollRequest);

}
