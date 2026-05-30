package com.example.hr_managment_system.dto.Employee;

import java.time.LocalDateTime;

public record DocumentResponse(
        String documentId,
        String filename,
        String fileType,
        LocalDateTime uploadDate
) {}
