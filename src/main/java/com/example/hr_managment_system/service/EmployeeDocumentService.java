package com.example.hr_managment_system.service;

import com.example.hr_managment_system.dto.Employee.DocumentResponse;
import com.example.hr_managment_system.domain.EmployeeDocument;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface EmployeeDocumentService {
    DocumentResponse uploadDocument(String employeeId, MultipartFile file);
    List<DocumentResponse> getDocumentsByEmployeeId(String employeeId);
    EmployeeDocument getDocumentEntity(String documentId);
    void deleteDocument(String documentId);
}
