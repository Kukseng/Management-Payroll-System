# Frontend Developer - Quick Reference Guide

## HR Management System UI Implementation

### Quick Navigation
- [View Controllers](#view-controllers)
- [Thymeleaf Templates](#thymeleaf-templates)
- [API Endpoints](#api-endpoints-by-role)
- [Common Code Snippets](#common-code-snippets)
- [Troubleshooting](#troubleshooting)

---

## View Controllers

### ADMIN CONTROLLERS

#### AuthViewController
```java
GET /login              // Login page
GET /logout-page        // Logout confirmation
```

#### AdminDashboardViewController
```java
GET /admin/dashboard                    // Dashboard overview
GET /admin/employees                    // List all employees
GET /admin/employees/create             // Create form
GET /admin/employees/{id}               // Employee details
GET /admin/employees/{id}/edit          // Edit form
GET /admin/leaves/pending               // Pending leaves
GET /admin/attendance                   // Attendance report
GET /admin/payroll                      // Payroll overview
GET /admin/payroll/process              // Process payroll form
```

### HR/MANAGER CONTROLLERS

#### HRDashboardViewController
```java
GET /hr/dashboard                       // HR dashboard
GET /hr/leaves/pending                  // Pending leave requests
GET /hr/leaves/{id}                     // Leave details
GET /hr/attendance                      // Department attendance report
```

### EMPLOYEE CONTROLLERS

#### EmployeeDashboardViewController
```java
GET /employee/dashboard                 // Employee dashboard
GET /employee/profile                   // My profile
GET /employee/attendance                // My attendance records
GET /employee/attendance/clock-inout    // Clock in/out page
GET /employee/leaves                    // My leave requests
GET /employee/leaves/request            // Request leave form
GET /employee/leaves/balance            // Check leave balance
GET /employee/payroll                   // My payroll slips
GET /employee/performance               // My performance reviews
```

---

## Thymeleaf Templates

### Template File Structure

```
templates/
├── auth/
│   ├── login.html
│   └── logout.html
├── layout/
│   └── base.html
├── dashboard/
│   ├── admin-dashboard.html
│   ├── hr-dashboard.html
│   └── employee-dashboard.html
├── employee/
│   ├── list.html
│   ├── create.html
│   ├── edit.html
│   ├── detail.html
│   └── view.html
├── attendance/
│   ├── clock-inout.html
│   ├── my-attendance.html
│   ├── employee-attendance.html
│   ├── hr-report.html
│   └── admin-report.html
├── leave/
│   ├── request.html
│   ├── my-leaves.html
│   ├── pending.html
│   ├── balance.html
│   └── detail.html
├── payroll/
│   ├── my-payroll.html
│   ├── payroll-detail.html
│   ├── process.html
│   └── reports.html
└── performance/
    ├── my-reviews.html
    ├── create.html
    └── detail.html
```

### Basic Thymeleaf Syntax

#### Variables
```html
<span th:text="${employee.firstName}"></span>
<span th:text="${employee.firstName ?: 'Unknown'}"></span>
```

#### Conditionals
```html
<div th:if="${role == 'ADMIN'}">Admin content</div>
<div th:unless="${role == 'EMPLOYEE'}">Non-employee content</div>
```

#### Loops
```html
<tr th:each="emp : ${employees}">
    <td th:text="${emp.firstName}"></td>
</tr>
```

#### Links
```html
<a th:href="@{/admin/employees}">Employees</a>
<a th:href="@{/admin/employees/{id}(id=${emp.id})}">View Employee</a>
```

#### Forms
```html
<form th:action="@{/admin/employees}" method="post" th:object="${employee}">
    <input type="text" th:field="*{firstName}">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
    <button type="submit">Submit</button>
</form>
```

---

## API Endpoints by Role

### ADMIN ENDPOINTS

#### Employee Management
```
GET    /api/v1/employee                      // Get all employees
POST   /api/v1/employee                      // Create employee
GET    /api/v1/employee/{id}                 // Get employee by ID
PUT    /api/v1/employee/{id}                 // Update employee
PATCH  /api/v1/employee/{id}/role            // Change role
PATCH  /api/v1/employee/{id}/status          // Change status
PATCH  /api/v1/employee/{id}/reset-password  // Reset password
DELETE /api/v1/employee/{id}                 // Delete employee
```

#### Leave Management
```
GET    /api/v1/leave/pending                 // Get pending leaves
PATCH  /api/v1/leave/{id}/approve            // Approve leave
PATCH  /api/v1/leave/{id}/reject             // Reject leave
GET    /api/v1/leave/balance/{employeeId}    // Get leave balance
```

#### Attendance
```
GET    /api/v1/attendance/admin              // Get all attendance
GET    /api/v1/attendance/employees          // Get employee attendance
```

#### Payroll
```
GET    /api/v1/payroll/employee/{id}         // Get employee payroll
GET    /api/v1/payroll/employee/{id}/month   // Get payroll by month
POST   /api/v1/payroll/process               // Process payroll
```

### HR/MANAGER ENDPOINTS

```
GET    /api/v1/leave/pending                 // Get pending leaves
PATCH  /api/v1/leave/{id}/approve            // Approve leave
PATCH  /api/v1/leave/{id}/reject             // Reject leave
GET    /api/v1/attendance/hr                 // Get HR attendance report
GET    /api/v1/attendance/employees          // Get employee attendance
```

### EMPLOYEE ENDPOINTS

```
GET    /api/v1/employee/{id}                 // Get my profile
GET    /api/v1/attendance/my                 // Get my attendance
POST   /api/v1/attendance/clock-in           // Clock in
POST   /api/v1/attendance/clock-out          // Clock out
GET    /api/v1/leave/my                      // Get my leaves
POST   /api/v1/leave                         // Submit leave request
GET    /api/v1/leave/balance/{employeeId}    // Get leave balance
GET    /api/v1/payroll/employee/{id}         // Get my payroll
```

---

## Common Code Snippets

### 1. Fetch Employee List
```javascript
fetch('/api/v1/employee?isActive=true', {
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
})
.then(res => res.json())
.then(data => {
    // Process employee data
    console.log(data);
})
.catch(err => console.error('Error:', err));
```

### 2. Create Employee
```javascript
const formData = {
    username: 'john_doe',
    email: 'john@example.com',
    firstName: 'John',
    lastName: 'Doe',
    departmentId: 'dept-001',
    roleId: 'role-001'
};

fetch('/api/v1/employee', {
    method: 'POST',
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(formData)
})
.then(res => res.json())
.then(data => {
    alert('Employee created successfully');
    window.location.href = '/admin/employees';
})
.catch(err => alert('Error: ' + err));
```

### 3. Update Employee
```javascript
const employeeId = 'emp-001';
const updateData = {
    firstName: 'Jonathan',
    email: 'jonathan@example.com'
};

fetch(`/api/v1/employee/${employeeId}`, {
    method: 'PUT',
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(updateData)
})
.then(res => res.json())
.then(data => {
    alert('Employee updated successfully');
    location.reload();
})
.catch(err => alert('Error: ' + err));
```

### 4. Approve Leave
```javascript
const leaveId = 'leave-001';
const remarks = 'Approved';

fetch(`/api/v1/leave/${leaveId}/approve`, {
    method: 'PATCH',
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({ remarks })
})
.then(res => res.json())
.then(data => {
    alert('Leave approved');
    location.reload();
})
.catch(err => alert('Error: ' + err));
```

### 5. Clock In
```javascript
const clockInData = {
    employeeId: 'emp-001',
    timestamp: new Date().toISOString()
};

fetch('/api/v1/attendance/clock-in', {
    method: 'POST',
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(clockInData)
})
.then(res => res.json())
.then(data => {
    alert('Clock in successful');
    // Hide clock-in button, show clock-out button
})
.catch(err => alert('Error: ' + err));
```

### 6. Get Leave Balance
```javascript
const employeeId = 'emp-001';

fetch(`/api/v1/leave/balance/${employeeId}`, {
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
})
.then(res => res.json())
.then(data => {
    console.log('Annual:', data.annualBalance);
    console.log('Sick:', data.sickBalance);
    console.log('Personal:', data.personalBalance);
})
.catch(err => console.error('Error:', err));
```

### 7. Delete Employee
```javascript
const employeeId = 'emp-001';

fetch(`/api/v1/employee/${employeeId}`, {
    method: 'DELETE',
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
})
.then(res => {
    if (res.ok) {
        alert('Employee deleted successfully');
        window.location.href = '/admin/employees';
    } else {
        alert('Error deleting employee');
    }
})
.catch(err => alert('Error: ' + err));
```

### 8. Show Toast Notification
```javascript
function showNotification(message, type = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const contentArea = document.querySelector('.content');
    contentArea.insertBefore(alertDiv, contentArea.firstChild);
    
    setTimeout(() => alertDiv.remove(), 5000);
}

// Usage
showNotification('Success!', 'success');
showNotification('Error!', 'danger');
```

---

## Common HTML Patterns

### Status Badge
```html
<span class="badge" 
      th:classappend="${item.status == 'ACTIVE'} ? 'bg-success' : 'bg-danger'"
      th:text="${item.status}"></span>
```

### Action Buttons
```html
<td>
    <a th:href="@{/admin/employees/{id}(id=${emp.id})}" class="btn btn-sm btn-info">
        <i class="fas fa-eye"></i> View
    </a>
    <a th:href="@{/admin/employees/{id}/edit(id=${emp.id})}" class="btn btn-sm btn-warning">
        <i class="fas fa-edit"></i> Edit
    </a>
    <button class="btn btn-sm btn-danger" onclick="deleteItem(this)" th:attr="data-id=${emp.id}">
        <i class="fas fa-trash"></i> Delete
    </button>
</td>
```

### Loading Spinner
```html
<div id="spinner" class="spinner" style="display:none;">
    <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
    </div>
</div>
```

### Empty State
```html
<tr th:if="${items == null or items.size() == 0}">
    <td colspan="5" class="text-center text-muted">
        <i class="fas fa-inbox"></i> No items found
    </td>
</tr>
```

---

## Troubleshooting

### Issue 1: CSRF Token Missing
**Error:** `Invalid CSRF token`
**Solution:** Add to forms:
```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
```

### Issue 2: 403 Forbidden
**Error:** `HTTP 403 - Access Denied`
**Solution:** Check user role in SecurityConfig

### Issue 3: Template Not Found
**Error:** `Whitelabel Error Page`
**Solution:** 
- Check template file exists in `src/main/resources/templates/`
- Verify return statement matches file name (no .html)
- Check application.properties has thymeleaf config

### Issue 4: WebJars CSS/JS Not Loading
**Error:** 404 on `/webjars/bootstrap/...`
**Solution:** Ensure WebJars are in build.gradle and use Thymeleaf path helper:
```html
<link th:href="@{/webjars/bootstrap/5.3.0/css/bootstrap.min.css}" rel="stylesheet">
```

### Issue 5: JavaScript API Calls Failing
**Error:** `401 Unauthorized`
**Solution:** Ensure JWT token is in localStorage and passed correctly:
```javascript
headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
}
```

### Issue 6: Table Data Not Displaying
**Error:** Empty table
**Solution:**
- Verify controller passes model attribute
- Check Thymeleaf loop syntax: `th:each="item : ${items}"`
- Ensure data is not null

---

## Security Best Practices

1. **Always validate input** both client and server-side
2. **Use CSRF tokens** in forms
3. **Validate JWT tokens** on every request
4. **Use HTTPS** in production
5. **Sanitize user input** to prevent XSS
6. **Implement proper authorization** checks
7. **Log security events** for audit trails
8. **Use strong passwords** for test accounts

---

## Performance Tips

1. **Lazy load images** using `loading="lazy"`
2. **Minify CSS/JS** in production
3. **Cache static assets** (WebJars)
4. **Use pagination** for large datasets
5. **Implement search/filter** on client-side when possible
6. **Debounce API calls** in search inputs
7. **Use async/await** instead of Promise chains

---

## Testing Templates

### Mock Data for Testing
```javascript
const mockEmployee = {
    id: 'emp-001',
    username: 'john_doe',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john@example.com',
    departmentName: 'Engineering',
    roleName: 'Software Engineer',
    isActive: true
};

const mockLeaves = [
    {
        id: 'leave-001',
        employeeId: 'emp-001',
        leaveType: 'ANNUAL',
        startDate: '2026-05-01',
        endDate: '2026-05-05',
        status: 'PENDING'
    }
];
```

### Testing with Postman
1. Import API collection from Swagger UI: `http://localhost:9090/swagger-ui.html`
2. Test endpoints with token-based authentication
3. Use environment variables for base URL and token

---

## Useful Resources

- [Bootstrap 5 Documentation](https://getbootstrap.com/)
- [Thymeleaf Official Guide](https://www.thymeleaf.org/)
- [Spring Security Docs](https://spring.io/projects/spring-security)
- [Font Awesome Icons](https://fontawesome.com/icons)
- [MDN Web Docs](https://developer.mozilla.org/)

---

## Support

For issues or questions:
1. Check the [THYMELEAF-IMPLEMENTATION-GUIDE.md](THYMELEAF-IMPLEMENTATION-GUIDE.md)
2. Review [JUNIT-TESTING-GUIDE.md](JUNIT-TESTING-GUIDE.md)
3. Check API documentation at `/swagger-ui.html`
4. Review [ENDPOINT-INVENTORY.md](ENDPOINT-INVENTORY.md) for available endpoints

