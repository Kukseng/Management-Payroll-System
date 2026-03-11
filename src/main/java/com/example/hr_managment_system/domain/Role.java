package com.example.hr_managment_system.domain;


import com.example.hr_managment_system.util.RoleUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String roleId;

    @Column(name = "role_name", nullable = false, unique = true)
    private RoleUtil roleName;

    @Column(name = "permission")
    private String permission;

}
