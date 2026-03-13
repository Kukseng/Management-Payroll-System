package com.example.hr_managment_system.dto.Payroll;

public record PayrollResponse(

        String payrollId,
        String employeeId,
        String employeeFirstName,
        String employeeLastName,
        Integer month,
        Integer year,
        Double deductions,
        Double grossSalary,
        Double netPay,
        String status

) {
}
// private Integer id;
//    private Integer employeeId;
//    private String employeeFirstName;
//    private String employeeLastName;
//    private Integer month;
//    private Integer year;
//    private Double grossSalary;
//    private Double deductions;
//    private Double netPay;
//    private String status;
//    private LocalDateTime processedAt;