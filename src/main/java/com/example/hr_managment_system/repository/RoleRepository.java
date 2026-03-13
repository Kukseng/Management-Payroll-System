package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Role;
import com.example.hr_managment_system.util.RoleUtil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    boolean existsByRoleName(RoleUtil roleName);

    Optional<Role> findByRoleName(RoleUtil roleName);

    Optional<Role> findByRoleId(String roleId);

}
