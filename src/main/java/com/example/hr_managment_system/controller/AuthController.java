package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.dto.Auth.AuthMeResponse;
import com.example.hr_managment_system.dto.Auth.ChangePasswordRequest;
import com.example.hr_managment_system.dto.Auth.LoginRequest;
import com.example.hr_managment_system.dto.Auth.LoginResponse;
import com.example.hr_managment_system.dto.Auth.LogoutRequest;
import com.example.hr_managment_system.dto.Auth.RefreshTokenRequest;
import com.example.hr_managment_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public AuthMeResponse me(Authentication authentication) {
        return authService.me(authentication.getName());
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody(required = false) LogoutRequest request) {
        authService.logout(request);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
    }
}
