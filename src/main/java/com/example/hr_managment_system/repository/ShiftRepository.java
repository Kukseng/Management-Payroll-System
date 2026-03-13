package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, String> {
}
