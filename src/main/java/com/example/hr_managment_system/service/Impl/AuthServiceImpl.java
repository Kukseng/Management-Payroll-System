package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.dto.Auth.AuthMeResponse;
import com.example.hr_managment_system.dto.Auth.ChangePasswordRequest;
import com.example.hr_managment_system.dto.Auth.LoginRequest;
import com.example.hr_managment_system.dto.Auth.LoginResponse;
import com.example.hr_managment_system.dto.Auth.LogoutRequest;
import com.example.hr_managment_system.dto.Auth.RefreshTokenRequest;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.security.JwtService;
import com.example.hr_managment_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);

        return new LoginResponse(accessToken, "Bearer");
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        if (!StringUtils.hasText(request.currentPassword()) || !StringUtils.hasText(request.newPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current and new password are required");
        }

        Employee employee = findActiveEmployee(username);

        if (!passwordEncoder.matches(request.currentPassword(), employee.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        employee.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        employeeRepository.save(employee);
    }

    @Override
    public AuthMeResponse me(String username) {
        Employee employee = findActiveEmployee(username);
        return new AuthMeResponse(
                employee.getEmployeeId(),
                employee.getUsername(),
                employee.getEmail(),
                employee.getRole().getRoleName().name(),
                employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null
        );
    }

    @Override
    public LoginResponse refresh(RefreshTokenRequest request) {
        if (request == null || !StringUtils.hasText(request.token())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
        }

        String username = jwtService.extractUsername(request.token());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(request.token(), userDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid or expired");
        }

        return new LoginResponse(jwtService.generateToken(userDetails), "Bearer");
    }

    @Override
    public void logout(LogoutRequest request) {
        // Stateless JWT logout is client-side unless token revocation storage is added.
    }

    private Employee findActiveEmployee(String principal) {
        return employeeRepository.findByUsernameAndIsActiveTrue(principal)
                .or(() -> employeeRepository.findByEmailAndIsActiveTrue(principal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
