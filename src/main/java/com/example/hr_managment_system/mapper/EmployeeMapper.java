package com.example.hr_managment_system.mapper;


import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeResponse employeeToEmployeeResponse(Employee employee);

    Employee employeeRequestToEmployee(EmployeeRequest employeeRequest);

}
