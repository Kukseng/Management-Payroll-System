package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Department;
import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.Role;
import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResetPasswordRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeRoleUpdateRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.dto.Employee.EmployeeStatusUpdateRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeUpdate;
import com.example.hr_managment_system.mapper.EmployeeMapper;
import com.example.hr_managment_system.repository.DepartmentRepository;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.repository.RoleRepository;
import com.example.hr_managment_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<EmployeeResponse> getAllEmployees(Boolean isActive) {
        return employeeRepository.findAll().stream()
                .filter(employee -> employee.getIsActive() == isActive)
                .map(employeeMapper::employeeToEmployeeResponse).toList();
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        if (!StringUtils.hasText(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }

        if (!StringUtils.hasText(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        if (employeeRepository.existsByEmail(request.email())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        if (employeeRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
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
        employee.setPasswordHash(passwordEncoder.encode(request.password()));
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

            if (request.departmentId() != null) {
                Department department = departmentRepository.findById(request.departmentId()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")
                );
                employee.setDepartment(department);
            }

            if (request.roleId() != null) {
                Role role = roleRepository.findByRoleId(request.roleId()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")
                );
                employee.setRole(role);
            }

            employeeRepository.save(employee);

        return employeeMapper.employeeToEmployeeResponse(employee) ;
    }

    @Override
    public EmployeeResponse updateEmployeeRole(String id, EmployeeRoleUpdateRequest request) {
        if (request == null || !StringUtils.hasText(request.roleId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleId is required");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        Role role = roleRepository.findByRoleId(request.roleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        employee.setRole(role);
        employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeResponse(employee);
    }

    @Override
    public EmployeeResponse updateEmployeeStatus(String id, EmployeeStatusUpdateRequest request) {
        if (request == null || request.isActive() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "isActive is required");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        employee.setIsActive(request.isActive());
        employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeResponse(employee);
    }

    @Override
    public void resetEmployeePassword(String id, EmployeeResetPasswordRequest request) {
        if (request == null || !StringUtils.hasText(request.newPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "newPassword is required");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        employee.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        employee.setIsActive(false);
        employeeRepository.save(employee);
    }
}
