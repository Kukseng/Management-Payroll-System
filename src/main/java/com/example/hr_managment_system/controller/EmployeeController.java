package com.example.hr_managment_system.controller;


import com.example.hr_managment_system.dto.Employee.EmployeeRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResetPasswordRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeRoleUpdateRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.dto.Employee.EmployeeStatusUpdateRequest;
import com.example.hr_managment_system.dto.Employee.EmployeeUpdate;
import com.example.hr_managment_system.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeResponse> getAllEmployees(@RequestParam(defaultValue ="true") Boolean isActive) {
        return employeeService.getAllEmployees(isActive);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@RequestBody EmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse getEmployeeById(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse updateEmployee(@PathVariable String id, @RequestBody EmployeeUpdate request) {
        return employeeService.updateEmployee(id, request);
    }

    @PatchMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse updateEmployeeRole(@PathVariable String id, @RequestBody EmployeeRoleUpdateRequest request) {
        return employeeService.updateEmployeeRole(id, request);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse updateEmployeeStatus(@PathVariable String id, @RequestBody EmployeeStatusUpdateRequest request) {
        return employeeService.updateEmployeeStatus(id, request);
    }

    @PatchMapping("/{id}/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetEmployeePassword(@PathVariable String id, @RequestBody EmployeeResetPasswordRequest request) {
        employeeService.resetEmployeePassword(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
    }

    @PostMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse uploadProfileImage(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return employeeService.uploadProfileImage(id, file);
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<?> getProfileImage(@PathVariable String id) {
        try {
            EmployeeResponse employee = employeeService.getEmployeeById(id);
            if (employee.profileImagePath() != null) {
                Path imagePath = Paths.get(employee.profileImagePath());
                if (Files.exists(imagePath) && Files.isReadable(imagePath)) {
                    Resource resource = new UrlResource(imagePath.toUri());
                    String contentType = Files.probeContentType(imagePath);
                    if (contentType == null) {
                        contentType = "image/png";
                    }
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(resource);
                }
            }
            // Dynamic initials avatar fallback
            String initialsUrl = "https://ui-avatars.com/api/?name=" + employee.firstName() + "+" + employee.lastName() + "&background=random&size=128";
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, initialsUrl)
                    .build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading profile image");
        }
    }
}
