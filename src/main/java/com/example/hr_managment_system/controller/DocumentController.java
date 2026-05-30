package com.example.hr_managment_system.controller;

import com.example.hr_managment_system.domain.EmployeeDocument;
import com.example.hr_managment_system.dto.Employee.DocumentResponse;
import com.example.hr_managment_system.service.EmployeeDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class DocumentController {

    private final EmployeeDocumentService documentService;

    @PostMapping(value = "/{employeeId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse uploadDocument(@PathVariable String employeeId,
                                           @RequestParam("file") MultipartFile file) {
        return documentService.uploadDocument(employeeId, file);
    }

    @GetMapping("/{employeeId}/documents")
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentResponse> getDocuments(@PathVariable String employeeId) {
        return documentService.getDocumentsByEmployeeId(employeeId);
    }

    @GetMapping("/{employeeId}/documents/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String employeeId,
                                                     @PathVariable String documentId) {
        try {
            EmployeeDocument doc = documentService.getDocumentEntity(documentId);
            Resource resource = new UrlResource(Paths.get(doc.getFilepath()).toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document file not found on server disk.");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(doc.getFileType() != null ? doc.getFileType() : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to resolve document path.");
        }
    }

    @DeleteMapping("/{employeeId}/documents/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable String employeeId,
                               @PathVariable String documentId) {
        documentService.deleteDocument(documentId);
    }
}
