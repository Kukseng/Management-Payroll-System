package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Department;
import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.Role;
import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.dto.Employee.EmployeeUpdate;
import com.example.hr_managment_system.mapper.EmployeeMapper;
import com.example.hr_managment_system.repository.DepartmentRepository;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.repository.RoleRepository;
import com.example.hr_managment_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeResponse> getAllEmployees(Boolean isActive) {
        return employeeRepository.findAll().stream()
                .filter(employee -> employee.getIsActive() == isActive)
                .map(employeeMapper::employeeToEmployeeResponse).toList();
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {


        if (employeeRepository.existsByEmail(request.email())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        Department department = departmentRepository.findById(request.departmentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")
        );

//        Department department = departmentRepository.findByDepartmentName(request.).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")
//        );
        Role role = roleRepository. findByRoleId(request.roleId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")
        );

        Employee employee = employeeMapper.employeeRequestToEmployee(request);
        employee.setIsActive(true);
        employee.setDepartment(department);
        employee.setRole(role);
        employee.setCreatedAt(LocalDateTime.now());

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeResponse(savedEmployee);
    }


    @Override
    public EmployeeResponse getEmployeeById(String id) {

        return employeeRepository.findById(id)
                .map(employeeMapper::employeeToEmployeeResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String id, EmployeeUpdate request) {

            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

                employeeMapper.updateEmployeeFromRequest(request, employee);
                employeeRepository.save(employee);

        return employeeMapper.employeeToEmployeeResponse(employee) ;
    }

    @Override
    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        employee.setIsActive(false);
        employeeRepository.save(employee);
    }
}
