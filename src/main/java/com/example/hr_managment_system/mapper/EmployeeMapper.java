package com.example.hr_managment_system.mapper;


import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.dto.Employee.EmployeeUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    void updateEmployeeFromRequest(EmployeeUpdate employeeUpdate, @MappingTarget Employee employee);


    @Mapping(source = "department.departmentId", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    @Mapping(source = "role.roleId", target = "roleId")
    @Mapping(source = "role.roleName", target = "roleName")
    EmployeeResponse employeeToEmployeeResponse(Employee employee);

    Employee employeeRequestToEmployee(EmployeeRequest employeeRequest);

}
