package com.example.hr_managment_system.service.Impl;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.EmployeeDocument;
import com.example.hr_managment_system.dto.Employee.DocumentResponse;
import com.example.hr_managment_system.repository.EmployeeDocumentRepository;
import com.example.hr_managment_system.repository.EmployeeRepository;
import com.example.hr_managment_system.service.EmployeeDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {

    private final EmployeeDocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;

    private static final String UPLOAD_DIR = "uploads/documents/";

    @Override
    public DocumentResponse uploadDocument(String employeeId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty and cannot be uploaded.");
        }

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found."));

        try {
            // Ensure target directory exists
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Create unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String cleanName = originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_") : "doc";
            String savedFilename = UUID.randomUUID().toString() + "_" + cleanName;

            Path targetPath = Paths.get(UPLOAD_DIR + savedFilename);
            Files.copy(file.getInputStream(), targetPath);

            EmployeeDocument document = new EmployeeDocument();
            document.setEmployee(employee);
            document.setFilename(originalFilename != null ? originalFilename : cleanName);
            document.setFilepath(targetPath.toString());
            document.setFileType(file.getContentType());
            document.setUploadDate(LocalDateTime.now());

            EmployeeDocument saved = documentRepository.save(document);
            log.info("Document successfully uploaded: {} for employee: {}", originalFilename, employeeId);

            return new DocumentResponse(
                    saved.getDocumentId(),
                    saved.getFilename(),
                    saved.getFileType(),
                    saved.getUploadDate()
            );

        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload document on the server.");
        }
    }

    @Override
    public List<DocumentResponse> getDocumentsByEmployeeId(String employeeId) {
        return documentRepository.findByEmployee_EmployeeIdOrderByUploadDateDesc(employeeId)
                .stream()
                .map(doc -> new DocumentResponse(
                        doc.getDocumentId(),
                        doc.getFilename(),
                        doc.getFileType(),
                        doc.getUploadDate()
                ))
                .toList();
    }

    @Override
    public EmployeeDocument getDocumentEntity(String documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found."));
    }

    @Override
    public void deleteDocument(String documentId) {
        EmployeeDocument document = getDocumentEntity(documentId);
        
        // Remove file from disk
        try {
            Path filePath = Paths.get(document.getFilepath());
            Files.deleteIfExists(filePath);
            log.info("Deleted document file from disk: {}", document.getFilepath());
        } catch (IOException e) {
            log.error("Failed to delete file from disk: {}, Error: {}", document.getFilepath(), e.getMessage());
        }

        // Remove from db
        documentRepository.delete(document);
        log.info("Deleted document from database: {}", documentId);
    }
}
