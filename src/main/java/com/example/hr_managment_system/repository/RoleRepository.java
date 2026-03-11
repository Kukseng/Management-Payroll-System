package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
