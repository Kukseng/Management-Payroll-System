package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Auth.ChangePasswordRequest;
import com.example.hr_managment_system.dto.Auth.AuthMeResponse;
import com.example.hr_managment_system.dto.Auth.LoginRequest;
import com.example.hr_managment_system.dto.Auth.LoginResponse;
import com.example.hr_managment_system.dto.Auth.LogoutRequest;
import com.example.hr_managment_system.dto.Auth.RefreshTokenRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void changePassword(String username, ChangePasswordRequest request);

    AuthMeResponse me(String username);

    LoginResponse refresh(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
