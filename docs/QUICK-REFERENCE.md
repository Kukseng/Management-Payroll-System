# Quick Reference Card - HR Management System API

## 🔐 Authentication
```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Response: { "token": "jwt_token_here", "username": "admin", "role": "ADMIN" }

# Use token in all requests:
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 📍 Base URL
```
http://localhost:8080/api/v1
```

---

## 👤 EMPLOYEE Endpoints (5 main features)

| Feature | Endpoint | Method | Returns |
|---------|----------|--------|---------|
| My Profile | `/employee/{id}` | GET | Employee details |
| Clock In | `/attendance/clock-in` | POST | Attendance record |
| Clock Out | `/attendance/clock-out` | POST | Attendance record |
| Submit Leave | `/leave` | POST | Leave request |
| My Leaves | `/leave/my` | GET | List of leaves |
| My Attendance | `/attendance/my` | GET | Attendance records |
| My Payroll | `/payroll/employee/{id}` | GET | Salary info |
| My Reviews | `/performance/{id}` | GET | Performance review |

---

## 👔 HR/MANAGER Endpoints (6 main features)

| Feature | Endpoint | Method | Returns |
|---------|----------|--------|---------|
| All Employees | `/employee?isActive=true` | GET | Employee list |
| Employee Detail | `/employee/{id}` | GET | Single employee |
| Pending Leaves | `/leave/pending` | GET | Leave requests to approve |
| Department Report | `/attendance/hr?dept&month&year` | GET | Attendance by dept |
| Create Review | `/performance` | POST | Performance review |
| Process Payroll | `/payroll/process` | POST | Process salary |
| View Payroll | `/payroll/employee/{id}` | GET | Salary records |

---

## 🛡️ ADMIN Endpoints (8 main features)

| Feature | Endpoint | Method | Returns |
|---------|----------|--------|---------|
| Create Employee | `/employee` | POST | New employee |
| Update Employee | `/employee/{id}` | PUT | Updated employee |
| Change Role | `/employee/{id}/role` | PATCH | Role updated |
| Change Status | `/employee/{id}/status` | PATCH | Status updated |
| Reset Password | `/employee/{id}/reset-password` | PATCH | Success |
| Delete Employee | `/employee/{id}` | DELETE | Success |
| All Attendance | `/attendance/admin?from&to&isActive` | GET | All records |
| All Employees | `/employee?isActive` | GET | All employees |

---

## 🔑 Role Hierarchy
```
ADMIN (Full Access)
  ├── Employee CRUD ✅
  ├── Role Management ✅
  ├── Attendance Reports ✅
  └── All HR Functions ✅

MANAGER/HR (HR Operations)
  ├── View Employees ✅
  ├── Manage Leaves ✅
  ├── Attendance Reports ✅
  └── Payroll Processing ✅

EMPLOYEE (Personal)
  ├── View Own Profile ✅
  ├── Attendance Tracking ✅
  ├── Leave Requests ✅
  └── View Payroll ✅
```

---

## 📋 Common Requests

### GET Employee
```bash
curl -X GET http://localhost:8080/api/v1/employee/emp-001 \
  -H "Authorization: Bearer TOKEN"
```

### Create Employee (Admin Only)
```bash
curl -X POST http://localhost:8080/api/v1/employee \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "Pass123!",
    "email": "john@company.com",
    "firstName": "John",
    "lastName": "Doe",
    "departmentId": "dept-001",
    "roleId": "role-001"
  }'
```

### Clock In
```bash
curl -X POST http://localhost:8080/api/v1/attendance/clock-in \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "emp-001",
    "timestamp": "2026-04-05T09:00:00"
  }'
```

### Submit Leave
```bash
curl -X POST http://localhost:8080/api/v1/leave \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "emp-001",
    "leaveType": "ANNUAL",
    "startDate": "2026-04-10",
    "endDate": "2026-04-12",
    "reason": "Vacation"
  }'
```

### Get Department Attendance (Manager)
```bash
curl -X GET "http://localhost:8080/api/v1/attendance/hr?departmentId=dept-001&month=4&year=2026" \
  -H "Authorization: Bearer MANAGER_TOKEN"
```

### Get All Attendance (Admin)
```bash
curl -X GET "http://localhost:8080/api/v1/attendance/admin?isActive=true" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

## ⚠️ HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | ✅ Success (GET) | Employee retrieved |
| 201 | ✅ Created (POST) | Employee created |
| 204 | ✅ No Content (DELETE) | Employee deleted |
| 400 | ❌ Bad Request | Invalid data |
| 401 | ❌ Unauthorized | Missing token |
| 403 | ❌ Forbidden | Insufficient role |
| 404 | ❌ Not Found | Employee not found |
| 500 | ❌ Server Error | Database error |

---

## 🛠️ Error Response Format
```json
{
  "status": 400,
  "message": "Descriptive error message"
}
```

---

## 🚀 Implementation Checklist for Frontends

### EMPLOYEE UI
- [ ] Login screen
- [ ] Dashboard (show profile)
- [ ] Clock in/out buttons
- [ ] Leave request form
- [ ] Leave history
- [ ] Attendance calendar
- [ ] Payroll viewer
- [ ] Performance reviews

### HR/MANAGER UI
- [ ] Employee list
- [ ] Employee search
- [ ] Department attendance report
- [ ] Leave approval interface ⚠️ (endpoint missing)
- [ ] Performance review creation
- [ ] Payroll processing
- [ ] Payroll reports

### ADMIN UI
- [ ] Employee management (create, edit, delete)
- [ ] Role assignment
- [ ] Status management
- [ ] All HR features
- [ ] System-wide attendance reports
- [ ] Department management ⚠️ (endpoint missing)
- [ ] Role management ⚠️ (endpoint missing)

---

## ⚠️ Known Limitations (Missing Endpoints)

1. **Leave Approval** - No approve/reject endpoint yet
   - Required for: HR/Manager leave approval workflow
   - Status: Planned for implementation

2. **Department Management** - No CRUD endpoints
   - Required for: Admin to create/manage departments
   - Status: Planned for implementation

3. **Role Management** - No CRUD endpoints
   - Required for: Admin to create/manage roles
   - Status: Planned for implementation

4. **Advanced Search** - Only isActive filter available
   - Required for: Search employees by name, department, role
   - Status: Planned for implementation

**For implementation details, see:** `IMPLEMENTATION-GUIDE.md`

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `FRONTEND-DEVELOPER-GUIDE.md` | Complete API reference |
| `ENDPOINT-INVENTORY.md` | What's built vs missing |
| `IMPLEMENTATION-GUIDE.md` | How to build missing features |
| `junit-test-analysis.md` | Test validation results |
| `DOCUMENTATION-SUMMARY.md` | Navigation guide |

---

## 💡 Tips

1. **Always include JWT token** in Authorization header
2. **Use ISO 8601 dates**: `2026-04-05T10:00:00`
3. **Check status codes** before processing response
4. **Role enforcement** happens at endpoint level (403 if insufficient role)
5. **Timestamps in responses** are in UTC format
6. **All endpoints** require authentication except `/auth/login`

---

## 🔗 Test the API

### Using Swagger UI (if enabled)
```
http://localhost:8080/swagger-ui.html
```

### Using Postman
1. Import endpoints from `FRONTEND-DEVELOPER-GUIDE.md`
2. Get JWT token from login endpoint
3. Use token in Authorization tab
4. Set environment variables for reuse

### Using curl (examples above)

---

**Last Updated:** April 5, 2026
**Status:** Ready for Development ✅
**Endpoints:** 17/27 Implemented (62%)

