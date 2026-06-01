package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
import com.example.hr_managment_system.mapper.EmployeeMapper;
import com.example.hr_managment_system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeProfileImageTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeImpl employeeService;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setEmployeeId("emp-001");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john@example.com");
    }

    @Test
    void uploadProfileImage_ShouldThrowException_WhenFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        assertThrows(ResponseStatusException.class, () -> 
            employeeService.uploadProfileImage("emp-001", emptyFile)
        );
    }

    @Test
    void uploadProfileImage_ShouldThrowException_WhenInvalidExtension() {
        MultipartFile invalidFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello".getBytes());

        assertThrows(ResponseStatusException.class, () -> 
            employeeService.uploadProfileImage("emp-001", invalidFile)
        );
    }

    @Test
    void uploadProfileImage_ShouldSaveImage_WhenFileIsValid() throws IOException {
        MultipartFile validFile = new MockMultipartFile("file", "test.png", "image/png", "FakeImageBytes".getBytes());
        when(employeeRepository.findById("emp-001")).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse mockResponse = new EmployeeResponse(
                "emp-001", "John", "Doe", "john@example.com", "johndoe", "Developer",
                "dept-1", "IT", "role-1", "ADMIN", "Full-time", 5000.0,
                java.time.LocalDate.of(1995, 5, 5), LocalDateTime.now(), true,
                "shift-1", "Morning", "uploads/profile-images/emp-001_mock.png"
        );
        when(employeeMapper.employeeToEmployeeResponse(any(Employee.class))).thenReturn(mockResponse);

        EmployeeResponse response = employeeService.uploadProfileImage("emp-001", validFile);

        assertNotNull(response);
        assertEquals("emp-001", response.employeeId());
        assertNotNull(response.profileImagePath());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
}
