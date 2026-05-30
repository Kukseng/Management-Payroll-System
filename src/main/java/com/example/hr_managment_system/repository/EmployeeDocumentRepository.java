package com.example.hr_managment_system.repository;

import com.example.hr_managment_system.domain.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, String> {
    List<EmployeeDocument> findByEmployee_EmployeeIdOrderByUploadDateDesc(String employeeId);
}
