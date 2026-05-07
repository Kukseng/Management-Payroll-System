package com.example.hr_managment_system.init;

import com.example.hr_managment_system.domain.Department;
import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.Role;
import com.example.hr_managment_system.repository.DepartmentRepository;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.repository.RoleRepository;
import com.example.hr_managment_system.util.EmplomentType;
import com.example.hr_managment_system.util.RoleUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DemoUserInitialize {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createDemoUsers() {
        createAdminIfMissing();
        createHrIfMissing();
        createEmployeeIfMissing();
    }

    private void createAdminIfMissing() {
        String username = "admin";
        if (employeeRepository.existsByUsername(username)) return;

        Role role = roleRepository.findByRoleName(RoleUtil.ADMIN).orElse(null);

        Employee admin = new Employee();
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail("admin@example.com");
        admin.setUsername(username);
        admin.setPasswordHash(passwordEncoder.encode("password"));
        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
        admin.setIsActive(true);
        admin.setRole(role);
        admin.setEmploymentType(EmplomentType.FULL_TIME);
        admin.setBaseSalary(5000d);
        admin.setCreatedAt(LocalDateTime.now());

        employeeRepository.save(admin);
    }

    private void createHrIfMissing() {
        String username = "hr";
        if (employeeRepository.existsByUsername(username)) return;

        Role role = roleRepository.findByRoleName(RoleUtil.MANAGER).orElse(null);
        Department hrDept = departmentRepository.findByDepartmentName("HR").orElse(null);

        Employee hr = new Employee();
        hr.setFirstName("Human");
        hr.setLastName("Resources");
        hr.setEmail("hr@example.com");
        hr.setUsername(username);
        hr.setPasswordHash(passwordEncoder.encode("password"));
        hr.setDateOfBirth(LocalDate.of(1990, 1, 1));
        hr.setIsActive(true);
        hr.setRole(role);
        hr.setDepartment(hrDept);
        hr.setEmploymentType(EmplomentType.FULL_TIME);
        hr.setBaseSalary(4000d);
        hr.setCreatedAt(LocalDateTime.now());

        employeeRepository.save(hr);
    }

    private void createEmployeeIfMissing() {
        String username = "employee";
        if (employeeRepository.existsByUsername(username)) return;

        Role role = roleRepository.findByRoleName(RoleUtil.EMPLOYEE).orElse(null);
        Department itDept = departmentRepository.findByDepartmentName("IT").orElse(null);

        Employee emp = new Employee();
        emp.setFirstName("Demo");
        emp.setLastName("User");
        emp.setEmail("employee@example.com");
        emp.setUsername(username);
        emp.setPasswordHash(passwordEncoder.encode("password"));
        emp.setDateOfBirth(LocalDate.of(1995, 6, 15));
        emp.setIsActive(true);
        emp.setRole(role);
        emp.setDepartment(itDept);
        emp.setEmploymentType(EmplomentType.FULL_TIME);
        emp.setBaseSalary(3000d);
        emp.setCreatedAt(LocalDateTime.now());

        employeeRepository.save(emp);
    }
}

