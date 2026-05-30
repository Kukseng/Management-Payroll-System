package com.example.hr_managment_system.service;

import com.example.hr_managment_system.domain.Employee;
import com.example.hr_managment_system.domain.Payroll;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class PdfService {

    public byte[] generatePayslipPdf(Payroll payroll) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Font Settings
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.NORMAL, new java.awt.Color(99, 102, 241));
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, new java.awt.Color(15, 23, 42));
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, new java.awt.Color(71, 85, 105));
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL, new java.awt.Color(15, 23, 42));

            // Main Title
            Paragraph title = new Paragraph("HR MANAGEMENT SYSTEM PAYSLIP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Invoice Details Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            Employee employee = payroll.getEmployee();
            String employeeName = (employee != null) ? (employee.getFirstName() + " " + employee.getLastName()) : "N/A";
            String email = (employee != null) ? employee.getEmail() : "N/A";
            String department = (employee != null && employee.getDepartment() != null) ? employee.getDepartment().getDepartmentName() : "N/A";
            String employeeId = (employee != null) ? employee.getEmployeeId() : "N/A";

            // Employee Info Cell
            PdfPCell empCell = new PdfPCell();
            empCell.setBorder(Rectangle.NO_BORDER);
            empCell.addElement(new Paragraph("EMPLOYEE INFORMATION", subtitleFont));
            empCell.addElement(new Paragraph("Name: " + employeeName, bodyFont));
            empCell.addElement(new Paragraph("Employee ID: " + employeeId, bodyFont));
            empCell.addElement(new Paragraph("Email: " + email, bodyFont));
            empCell.addElement(new Paragraph("Department: " + department, bodyFont));
            infoTable.addCell(empCell);

            // Payslip Metadata Cell
            PdfPCell metaCell = new PdfPCell();
            metaCell.setBorder(Rectangle.NO_BORDER);
            metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph metaTitle = new Paragraph("PAYMENT DETAILS", subtitleFont);
            metaTitle.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(metaTitle);
            
            String payPeriod = payroll.getMonth() + "/" + payroll.getYear();
            String processedDate = payroll.getProcessedAt() != null 
                    ? payroll.getProcessedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) 
                    : "N/A";

            Paragraph pPeriod = new Paragraph("Payment Period: " + payPeriod, bodyFont);
            pPeriod.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(pPeriod);

            Paragraph pProcessed = new Paragraph("Processed Date: " + processedDate, bodyFont);
            pProcessed.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(pProcessed);

            Paragraph pInvoice = new Paragraph("Reference ID: " + payroll.getPayrollId(), bodyFont);
            pInvoice.setAlignment(Element.ALIGN_RIGHT);
            metaCell.addElement(pInvoice);

            infoTable.addCell(metaCell);
            document.add(infoTable);

            // Earnings and Deductions Table
            PdfPTable financialTable = new PdfPTable(2);
            financialTable.setWidthPercentage(100);
            financialTable.setSpacingAfter(30);

            // Headers
            PdfPCell header1 = new PdfPCell(new Paragraph("Description", boldFont));
            header1.setBackgroundColor(new java.awt.Color(248, 250, 252));
            header1.setPadding(10);
            header1.setBorderColor(new java.awt.Color(226, 232, 240));
            financialTable.addCell(header1);

            PdfPCell header2 = new PdfPCell(new Paragraph("Amount", boldFont));
            header2.setBackgroundColor(new java.awt.Color(248, 250, 252));
            header2.setPadding(10);
            header2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            header2.setBorderColor(new java.awt.Color(226, 232, 240));
            financialTable.addCell(header2);

            // Base Salary
            double baseSalaryVal = employee != null && employee.getBaseSalary() != null ? employee.getBaseSalary() : 0.0;
            addFinancialRow(financialTable, "Base Salary", baseSalaryVal, bodyFont);

            // Overtime Pay
            double overtimeHours = payroll.getOvertimeHours() != null ? payroll.getOvertimeHours() : 0.0;
            double overtimePay = payroll.getOvertimePay() != null ? payroll.getOvertimePay() : 0.0;
            if (overtimeHours > 0) {
                addFinancialRow(financialTable, String.format("Overtime Pay (%s hrs)", String.format("%.2f", overtimeHours)), overtimePay, bodyFont);
            }

            // Deductions
            double deductions = payroll.getDeductions() != null ? payroll.getDeductions() : 0.0;
            addFinancialRow(financialTable, "Deductions (Taxes/Benefits)", -deductions, bodyFont);

            // Divider row
            PdfPCell divider = new PdfPCell();
            divider.setColspan(2);
            divider.setBorder(Rectangle.BOTTOM);
            divider.setBorderColor(new java.awt.Color(226, 232, 240));
            divider.setFixedHeight(2);
            financialTable.addCell(divider);

            // Net Salary
            PdfPCell netLabelCell = new PdfPCell(new Paragraph("NET DISBURSEMENT", boldFont));
            netLabelCell.setPadding(12);
            netLabelCell.setBorder(Rectangle.NO_BORDER);
            netLabelCell.setBackgroundColor(new java.awt.Color(241, 245, 249));
            financialTable.addCell(netLabelCell);

            double netPay = payroll.getNetPay() != null ? payroll.getNetPay() : 0.0;
            PdfPCell netAmountCell = new PdfPCell(new Paragraph("$" + String.format("%.2f", netPay), boldFont));
            netAmountCell.setPadding(12);
            netAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            netAmountCell.setBorder(Rectangle.NO_BORDER);
            netAmountCell.setBackgroundColor(new java.awt.Color(241, 245, 249));
            financialTable.addCell(netAmountCell);

            document.add(financialTable);

            // Footer Signature section
            Paragraph signature = new Paragraph("Authorized Signature: _______________________", bodyFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            signature.setSpacingBefore(50);
            document.add(signature);

            document.close();
            log.info("PDF payslip successfully generated in memory.");
        } catch (DocumentException e) {
            log.error("Failed to generate PDF document: {}", e.getMessage());
        }

        return out.toByteArray();
    }

    private void addFinancialRow(PdfPTable table, String label, double amount, Font font) {
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, font));
        labelCell.setPadding(8);
        labelCell.setBorderColor(new java.awt.Color(226, 232, 240));
        table.addCell(labelCell);

        String amountText = amount >= 0 
                ? "$" + String.format("%.2f", amount) 
                : "-$" + String.format("%.2f", Math.abs(amount));
        PdfPCell amountCell = new PdfPCell(new Paragraph(amountText, font));
        amountCell.setPadding(8);
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        amountCell.setBorderColor(new java.awt.Color(226, 232, 240));
        table.addCell(amountCell);
    }
}
