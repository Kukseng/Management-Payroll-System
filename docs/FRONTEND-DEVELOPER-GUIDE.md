# HR Management System - Frontend Developer Guide

## Overview
This guide provides frontend developers with all the API endpoints organized by user role (ADMIN, HR/MANAGER, EMPLOYEE) to build role-based UIs.

---

## Authentication

### Login Endpoint
```
POST /api/v1/auth/login
Content-Type: application/json

Request Body:
{
  "username": "string",
  "password": "string"
}

Response (200 OK):
{
  "token": "jwt_token_here",
  "username": "string",
  "role": "ADMIN|MANAGER|EMPLOYEE"
}
```

**Note:** Save the JWT token and include it in all subsequent requests:
```
Authorization: Bearer <jwt_token>
```

---

## Role-Based Access Control

| Role | Permissions |
|------|-----------|
| **ADMIN** | Full access to all endpoints. Can create, read, update, delete employees. Full attendance/payroll/performance management. |
| **MANAGER** (HR) | Can read employee data, manage leave requests, view attendance reports, manage payroll and performance reviews. Cannot delete employees. |
| **EMPLOYEE** | Can view own data, submit leave requests, view own attendance, view own performance reviews, view payroll. |

---

## API Endpoints by Role

### 1. EMPLOYEE UI Endpoints
These endpoints are accessible to EMPLOYEE role users for viewing and managing their own data.

#### Employee Dashboard
```
GET /api/v1/employee/{id}
Authorization: Bearer <token>

Response (200 OK):
{
  "id": "emp-001",
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "departmentId": "dept-001",
  "departmentName": "Engineering",
  "roleId": "role-001",
  "roleName": "Software Engineer",
  "isActive": true,
  "createdAt": "2026-04-01T10:00:00",
  "updatedAt": "2026-04-05T15:30:00"
}
```

#### My Attendance Records
```
GET /api/v1/attendance/my?from=2026-04-01&to=2026-04-05
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": "att-001",
    "employeeId": "emp-001",
    "clockInTime": "2026-04-05T09:00:00",
    "clockOutTime": "2026-04-05T17:00:00",
    "status": "PRESENT",
    "workHours": 8
  }
]
```

#### Clock In/Out
```
POST /api/v1/attendance/clock-in
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "employeeId": "emp-001",
  "timestamp": "2026-04-05T09:00:00"
}

Response (201 CREATED):
{
  "id": "att-001",
  "employeeId": "emp-001",
  "clockInTime": "2026-04-05T09:00:00",
  "status": "PRESENT"
}
```

```
POST /api/v1/attendance/clock-out
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "employeeId": "emp-001",
  "timestamp": "2026-04-05T17:00:00"
}

Response (201 CREATED):
{
  "id": "att-001",
  "employeeId": "emp-001",
  "clockOutTime": "2026-04-05T17:00:00",
  "status": "COMPLETED"
}
```

#### Submit Leave Request
```
POST /api/v1/leave
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "employeeId": "emp-001",
  "leaveType": "ANNUAL|SICK|PERSONAL",
  "startDate": "2026-04-10",
  "endDate": "2026-04-12",
  "reason": "Family emergency"
}

Response (201 CREATED):
{
  "id": "leave-001",
  "employeeId": "emp-001",
  "leaveType": "PERSONAL",
  "startDate": "2026-04-10",
  "endDate": "2026-04-12",
  "status": "PENDING",
  "createdAt": "2026-04-05T10:00:00"
}
```

#### View My Leave Requests
```
GET /api/v1/leave/my
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": "leave-001",
    "employeeId": "emp-001",
    "leaveType": "PERSONAL",
    "startDate": "2026-04-10",
    "endDate": "2026-04-12",
    "reason": "Family emergency",
    "status": "PENDING",
    "createdAt": "2026-04-05T10:00:00"
  }
]
```

#### View Own Payroll
```
GET /api/v1/payroll/employee/{employeeId}
Authorization: Bearer <token>

Response (200 OK):
{
  "id": "payroll-001",
  "employeeId": "emp-001",
  "month": 4,
  "year": 2026,
  "baseSalary": 50000,
  "allowances": 5000,
  "deductions": 2000,
  "netSalary": 53000,
  "status": "PROCESSED",
  "processedDate": "2026-04-01T10:00:00"
}
```

#### View Own Performance Review
```
GET /api/v1/performance/{id}
Authorization: Bearer <token>

Response (200 OK):
{
  "id": "perf-001",
  "employeeId": "emp-001",
  "reviewDate": "2026-04-01",
  "rating": 4.5,
  "feedback": "Excellent performance",
  "managerName": "Jane Smith",
  "reviewPeriod": "Q1 2026"
}
```

---

### 2. HR (MANAGER) UI Endpoints
These endpoints are accessible to MANAGER/HR role users for managing department data.

#### View All Employees
```
GET /api/v1/employee?isActive=true
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": "emp-001",
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "departmentId": "dept-001",
    "departmentName": "Engineering",
    "roleId": "role-001",
    "roleName": "Software Engineer",
    "isActive": true
  }
]
```

#### View Employee Details
```
GET /api/v1/employee/{id}
Authorization: Bearer <token>

Response (200 OK):
{
  "id": "emp-001",
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "departmentId": "dept-001",
  "departmentName": "Engineering",
  "roleId": "role-001",
  "roleName": "Software Engineer",
  "isActive": true,
  "createdAt": "2026-04-01T10:00:00",
  "updatedAt": "2026-04-05T15:30:00"
}
```

#### HR Attendance Report (by Department)
```
GET /api/v1/attendance/hr?departmentId=dept-001&month=4&year=2026
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": "att-001",
    "employeeId": "emp-001",
    "employeeName": "John Doe",
    "clockInTime": "2026-04-05T09:00:00",
    "clockOutTime": "2026-04-05T17:00:00",
    "status": "PRESENT",
    "workHours": 8,
    "date": "2026-04-05"
  }
]
```

#### View Pending Leave Requests
```
GET /api/v1/leave/pending
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": "leave-001",
    "employeeId": "emp-001",
    "employeeName": "John Doe",
    "leaveType": "PERSONAL",
    "startDate": "2026-04-10",
    "endDate": "2026-04-12",
    "reason": "Family emergency",
    "status": "PENDING",
    "createdAt": "2026-04-05T10:00:00"
  }
]
```

#### Approve/Reject Leave Request
```
PATCH /api/v1/leave/{id}/status
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "status": "APPROVED|REJECTED",
  "remarks": "Approved for family emergency"
}

Response (200 OK):
{
  "id": "leave-001",
  "status": "APPROVED",
  "remarks": "Approved for family emergency",
  "updatedAt": "2026-04-05T14:00:00"
}
```

#### Create Performance Review
```
POST /api/v1/performance
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "employeeId": "emp-001",
  "rating": 4.5,
  "feedback": "Excellent performance",
  "reviewPeriod": "Q1 2026"
}

Response (201 CREATED):
{
  "id": "perf-001",
  "employeeId": "emp-001",
  "rating": 4.5,
  "feedback": "Excellent performance",
  "reviewPeriod": "Q1 2026",
  "createdAt": "2026-04-05T10:00:00"
}
```

#### View Performance Reviews
```
GET /api/v1/performance/{id}
Authorization: Bearer <token>

Response (200 OK):
{
  "id": "perf-001",
  "employeeId": "emp-001",
  "employeeName": "John Doe",
  "rating": 4.5,
  "feedback": "Excellent performance",
  "reviewPeriod": "Q1 2026",
  "createdAt": "2026-04-05T10:00:00"
}
```

#### Process Payroll
```
POST /api/v1/payroll/process
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "month": 4,
  "year": 2026,
  "employeeIds": ["emp-001", "emp-002", "emp-003"]
}

Response (200 OK):
{
  "processedCount": 3,
  "failedCount": 0,
  "message": "Payroll processed successfully"
}
```

#### View Payroll (All Employees)
```
GET /api/v1/payroll/employee/{employeeId}?month=4&year=2026
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": "payroll-001",
    "employeeId": "emp-001",
    "employeeName": "John Doe",
    "month": 4,
    "year": 2026,
    "baseSalary": 50000,
    "allowances": 5000,
    "deductions": 2000,
    "netSalary": 53000,
    "status": "PROCESSED"
  }
]
```

---

### 3. ADMIN UI Endpoints
These endpoints are accessible only to ADMIN role users for full system management.

#### Create New Employee
```
POST /api/v1/employee
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "username": "jane_smith",
  "password": "SecurePassword123!",
  "email": "jane@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "departmentId": "dept-001",
  "roleId": "role-002"
}

Response (201 CREATED):
{
  "id": "emp-002",
  "username": "jane_smith",
  "email": "jane@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "departmentId": "dept-001",
  "departmentName": "Engineering",
  "roleId": "role-002",
  "roleName": "Manager",
  "isActive": true,
  "createdAt": "2026-04-05T10:00:00"
}
```

#### Update Employee (Full Update)
```
PUT /api/v1/employee/{id}
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "departmentId": "dept-002"
}

Response (200 OK):
{
  "id": "emp-002",
  "username": "jane_smith",
  "email": "jane.smith@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "departmentId": "dept-002",
  "departmentName": "Management",
  "roleId": "role-002",
  "roleName": "Manager",
  "isActive": true,
  "updatedAt": "2026-04-05T15:00:00"
}
```

#### Update Employee Role
```
PATCH /api/v1/employee/{id}/role
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "roleId": "role-003"
}

Response (200 OK):
{
  "id": "emp-002",
  "username": "jane_smith",
  "roleId": "role-003",
  "roleName": "Senior Manager",
  "updatedAt": "2026-04-05T15:00:00"
}
```

#### Update Employee Status (Activate/Deactivate)
```
PATCH /api/v1/employee/{id}/status
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "isActive": false
}

Response (200 OK):
{
  "id": "emp-002",
  "username": "jane_smith",
  "isActive": false,
  "updatedAt": "2026-04-05T15:00:00"
}
```

#### Reset Employee Password
```
PATCH /api/v1/employee/{id}/reset-password
Authorization: Bearer <token>
Content-Type: application/json

Request Body:
{
  "newPassword": "NewSecurePassword456!"
}

Response (204 NO CONTENT)
```

#### Delete Employee (Deactivate)
```
DELETE /api/v1/employee/{id}
Authorization: Bearer <token>

Response (204 NO CONTENT)
```

#### Admin Attendance Report (All Employees)
```
GET /api/v1/attendance/admin?from=2026-04-01&to=2026-04-30&isActive=true
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": "att-001",
    "employeeId": "emp-001",
    "employeeName": "John Doe",
    "departmentName": "Engineering",
    "clockInTime": "2026-04-05T09:00:00",
    "clockOutTime": "2026-04-05T17:00:00",
    "status": "PRESENT",
    "workHours": 8,
    "date": "2026-04-05"
  }
]
```

#### View All Employees (with inactive)
```
GET /api/v1/employee?isActive=false
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": "emp-003",
    "username": "old_employee",
    "email": "old@example.com",
    "firstName": "Old",
    "lastName": "Employee",
    "departmentId": "dept-001",
    "roleId": "role-001",
    "isActive": false,
    "createdAt": "2025-01-01T10:00:00",
    "deactivatedAt": "2026-04-01T10:00:00"
  }
]
```

---

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Invalid request parameters"
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Invalid or missing authentication token"
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "message": "You don't have permission to access this resource"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "Internal server error"
}
```

---

## Frontend Implementation Guide

### Step 1: Authentication Flow
1. Create a login form with username and password fields
2. Call POST `/api/v1/auth/login`
3. Store the JWT token in localStorage/sessionStorage
4. Redirect to dashboard based on user role

### Step 2: Role-Based UI Components
Create conditional rendering based on user role:

```javascript
// Example: React component
const Dashboard = () => {
  const userRole = localStorage.getItem('userRole'); // ADMIN, MANAGER, EMPLOYEE
  
  if (userRole === 'ADMIN') {
    return <AdminDashboard />;
  } else if (userRole === 'MANAGER') {
    return <HRDashboard />;
  } else {
    return <EmployeeDashboard />;
  }
};
```

### Step 3: API Integration
Include JWT token in all requests:

```javascript
// Example: Fetch call
const response = await fetch('/api/v1/employee/emp-001', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`,
    'Content-Type': 'application/json'
  }
});
```

### Step 4: UI Screens to Build

#### For EMPLOYEE Role:
- ✅ Dashboard (view own profile)
- ✅ Attendance (clock in/out, view history)
- ✅ Leave Management (submit, view history)
- ✅ Payroll (view salary details)
- ✅ Performance (view reviews)

#### For HR/MANAGER Role:
- ✅ Employee List (view all active employees)
- ✅ Employee Details (view individual employee)
- ✅ Department Attendance Report
- ✅ Leave Requests (approve/reject)
- ✅ Performance Management (create, view reviews)
- ✅ Payroll Management (process, view)

#### For ADMIN Role:
- ✅ Employee Management (CRUD operations)
- ✅ Role Management (assign roles)
- ✅ All HR functions
- ✅ System Reports (all attendance)
- ✅ User Management

---

## Testing the Endpoints

You can test all endpoints using curl or Postman. Here's an example:

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Get all employees (requires ADMIN/MANAGER token)
curl -X GET http://localhost:8080/api/v1/employee \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Clock in (requires EMPLOYEE token)
curl -X POST http://localhost:8080/api/v1/attendance/clock-in \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"employeeId":"emp-001","timestamp":"2026-04-05T09:00:00"}'
```

---

## Important Notes

1. **JWT Token**: Always include the Authorization header with your JWT token
2. **Timestamps**: Use ISO 8601 format for all date/time fields
3. **Role Enforcement**: The API enforces role-based access control at the endpoint level
4. **Error Handling**: Always implement proper error handling in your UI
5. **Pagination**: Some endpoints support pagination (check Swagger for details)
6. **Date Filtering**: Date filters are optional unless explicitly required
7. **Status Codes**: Always check HTTP status codes for success/failure

