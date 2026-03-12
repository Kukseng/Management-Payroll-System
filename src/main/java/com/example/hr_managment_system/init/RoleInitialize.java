package com.example.hr_managment_system.init;

import com.example.hr_managment_system.domain.Role;
import com.example.hr_managment_system.repository.RoleRepository;
import com.example.hr_managment_system.util.RoleUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleInitialize {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void initializeCustomerSegment() {
        List<Role> missingRoles = new ArrayList<>();

        addRoleIfMissing(missingRoles, RoleUtil.ADMIN, "Get,Post");
        addRoleIfMissing(missingRoles, RoleUtil.MANAGER, "Get,Post,Put");
        addRoleIfMissing(missingRoles, RoleUtil.EMPLOYEE, "Get");

        if (!missingRoles.isEmpty()) {
            roleRepository.saveAll(missingRoles);
        }
    }

    private void addRoleIfMissing(List<Role> missingRoles, RoleUtil roleName, String permission) {
        if (roleRepository.existsByRoleName(roleName)) {
            return;
        }

        Role role = new Role();
        role.setRoleName(roleName);
        role.setPermission(permission);
        missingRoles.add(role);
    }
}
