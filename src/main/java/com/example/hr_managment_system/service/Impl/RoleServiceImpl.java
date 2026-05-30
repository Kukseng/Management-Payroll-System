package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.dto.Role.RoleResponse;
import com.example.hr_managment_system.repository.RoleRepository;
import com.example.hr_managment_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(
                        role.getRoleId(),
                        role.getRoleName() != null ? role.getRoleName().name() : null,
                        role.getPermission()
                ))
                .toList();
    }
}
