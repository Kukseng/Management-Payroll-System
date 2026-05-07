//package com.example.hr_managment_system.service.Impl;
//
//
//import com.example.hr_managment_system.domain.Employee;
//import com.example.hr_managment_system.dto.Employee.EmployeeResponse;
//import com.example.hr_managment_system.mapper.EmployeeMapper;
//import com.example.hr_managment_system.repository.EmployeeRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class EmployeeImplTest {
//
//    @Mock
//    private EmployeeRepository employeeRepository; // The dependency inside EmployeeImpl
//
//    @InjectMocks
//    private EmployeeMapper employeeMapper;
//
//
//    @InjectMocks
//    private EmployeeImpl employeeService; // The real service being tested
//
//    @Test
//    void getEmployeeById_ShouldThrowException_WhenIdIsNull() {
//        // Test should expect ResponseStatusException for null/empty IDs
//        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
//            employeeService.getEmployeeById("2324");
//        });
//
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
//        assertEquals("ID is required", exception.getReason());
//    }
//
//    @Test
//    void getEmployeeById_ShouldReturnEmployee_WhenIdExists() {
//        // Given
//        String employeeId = "emp-001";
//        Employee mockEmployee = new Employee()
//        mockEmployee.setId(employeeId);
//
//        when(employeeRepository.findById(employeeId))
//                .thenReturn(Optional.of(mockEmployee));
//
//        EmployeeResponse mockResponse = new EmployeeResponse(/* ... */);
//        when(employeeMapper.employeeToEmployeeResponse(mockEmployee))
////                .thenReturn(mockResponse);
//
//        // When
//        EmployeeResponse result = employeeService.getEmployeeById(employeeId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(employeeId, result.getId());
//        verify(employeeRepository, times(1)).findById(employeeId);
//    }
//
//}