package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Role.RoleResponse;
import java.util.List;

public interface RoleService {
    List<RoleResponse> getAllRoles();
}
