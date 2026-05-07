package com.example.hr_managment_system.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/logout-page")
    public String logoutPage() {
        return "auth/logout";
    }

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "auth/change-password";
    }
}

