package com.example.hr_managment_system.security;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsernameAndIsActiveTrue(username)
                .or(() -> employeeRepository.findByEmailAndIsActiveTrue(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(employee.getEmail())
                .password(employee.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + employee.getRole().getRoleName().name())))
                .build();
    }
}
