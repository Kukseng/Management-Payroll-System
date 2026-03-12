package com.example.hr_managment_system.mapper;


import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.dto.Employee.EmployeeUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    void updateEmployeeFromRequest(EmployeeUpdate employeeUpdate, @MappingTarget Employee employee);

    EmployeeResponse employeeToEmployeeResponse(Employee employee);

    Employee employeeRequestToEmployee(EmployeeRequest employeeRequest);

}
