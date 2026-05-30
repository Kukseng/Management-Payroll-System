package com.example.hr_managment_system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private static final String LOG_DIR = "logs/emails/";

    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to: {}, Subject: {}", to, subject);
        boolean sentSuccessfully = false;

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("Email successfully sent via JavaMailSender.");
                sentSuccessfully = true;
            } catch (Exception e) {
                log.error("Failed to send email via SMTP, falling back to simulated file email. Error: {}", e.getMessage());
            }
        } else {
            log.info("JavaMailSender not configured. Falling back to simulated file email.");
        }

        if (!sentSuccessfully) {
            writeSimulatedEmailToFile(to, subject, body);
        }
    }

    private void writeSimulatedEmailToFile(String to, String subject, String body) {
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String safeTo = to.replaceAll("[^a-zA-Z0-9.-]", "_");
            File file = new File(dir, "email_" + timestamp + "_" + safeTo + ".txt");

            try (FileWriter writer = new FileWriter(file)) {
                writer.write("==================================================\n");
                writer.write("SIMULATED OUTGOING EMAIL\n");
                writer.write("Timestamp: " + LocalDateTime.now() + "\n");
                writer.write("To:        " + to + "\n");
                writer.write("Subject:   " + subject + "\n");
                writer.write("==================================================\n\n");
                writer.write(body);
                writer.write("\n\n==================================================\n");
            }
            log.info("Simulated email saved to file: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write simulated email to file: {}", e.getMessage());
        }
    }

    public void sendLeaveRequestEmail(String managerEmail, String employeeName, String leaveType, String startDate, String endDate) {
        String subject = "New Leave Request: " + employeeName;
        String body = String.format(
                "Hello HR/Manager,\n\n" +
                "A new leave request has been submitted by %s.\n\n" +
                "Details:\n" +
                "- Leave Type: %s\n" +
                "- Start Date: %s\n" +
                "- End Date:   %s\n\n" +
                "Please review the request on the HR Dashboard.\n\n" +
                "Best regards,\n" +
                "HR Management System",
                employeeName, leaveType, startDate, endDate
        );
        sendEmail(managerEmail, subject, body);
    }

    public void sendLeaveStatusEmail(String employeeEmail, String employeeName, String status, String remarks) {
        String subject = "Leave Request Update: " + status;
        String body = String.format(
                "Hello %s,\n\n" +
                "Your leave request status has been updated.\n\n" +
                "New Status: %s\n" +
                "Remarks:    %s\n\n" +
                "Best regards,\n" +
                "HR Management System",
                employeeName, status, remarks != null ? remarks : "None"
        );
        sendEmail(employeeEmail, subject, body);
    }

    public void sendPayrollProcessedEmail(String employeeEmail, String employeeName, String monthYear, double netPay) {
        String subject = "Payslip Processed: " + monthYear;
        String body = String.format(
                "Hello %s,\n\n" +
                "Your payslip for %s has been successfully processed.\n\n" +
                "Net Pay: $%s\n\n" +
                "You can view details and download the PDF copy directly from your Compensation/My Payroll dashboard.\n\n" +
                "Best regards,\n" +
                "HR Management System",
                employeeName, monthYear, String.format("%.2f", netPay)
        );
        sendEmail(employeeEmail, subject, body);
    }
}
