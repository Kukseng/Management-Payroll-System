# HR Management System API

Spring Boot HR backend with JWT authentication and role-based endpoint protection.

## Checklist

- [x] JWT login endpoint
- [x] Protected endpoints with `hasRole` / `hasAnyRole`
- [x] Employee account management endpoints (role, status, reset password)
- [x] Leave request and approval endpoints
- [ ] Refresh/logout token revocation (currently stateless placeholder)
- [ ] Complete API tests for all new endpoints

## Tech Stack

- Java 21
- Spring Boot
- Spring Security (stateless JWT)
- Spring Data JPA
- PostgreSQL
- MapStruct

## Project Structure

Key packages under `src/main/java/com/example/hr_managment_system`:

- `controller` - REST controllers
- `service` / `service.Impl` - business logic
- `domain` - JPA entities
- `repository` - Spring Data repositories
- `security` - JWT + security config
- `dto` - request/response payloads
- `mapper` - mapping data

## Configuration


## Run

```fish
./gradlew clean bootRun
```

## Build and Test

```fish
./gradlew compileJava
./gradlew test
```

## Endpoint Smoke Test (fish)

A quick endpoint-level smoke test is available at `scripts/smoke_test_endpoints.fish`.

1) Install tools

```fish
sudo apt-get update
sudo apt-get install -y curl jq
```

2) Make the script executable

```fish
chmod +x scripts/smoke_test_endpoints.fish
```

## Authentication Flow

1. Create employee account (admin endpoint) with `username`, `password`, role.
2. Login with username/password at `/api/v1/auth/login`.
3. Use returned token in `Authorization` header:

```text
Authorization: Bearer <accessToken>
```

## Main Endpoints

Base URL: `http://localhost:9090`

### Auth

- `POST /api/v1/auth/login` (public)
- `GET /api/v1/auth/me` (authenticated)
- `POST /api/v1/auth/change-password` (authenticated)
- `POST /api/v1/auth/refresh` (authenticated in current security config)
- `POST /api/v1/auth/logout` (authenticated, no server revocation yet)

Example login request:

```json
{
  "username": "admin.user",
  "password": "admin123"
}
```

### Employee

- `GET /api/v1/employee`
- `POST /api/v1/employee`
- `GET /api/v1/employee/{id}`
- `PUT /api/v1/employee/{id}`
- `PATCH /api/v1/employee/{id}/role`
- `PATCH /api/v1/employee/{id}/status`
- `PATCH /api/v1/employee/{id}/reset-password`
- `DELETE /api/v1/employee/{id}`

Example create employee request:

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@company.com",
  "username": "john.doe",
  "password": "StrongPass123!",
  "dateOfBirth": "1996-04-10",
  "departmentId": "<departmentId>",
  "roleId": "<roleId>",
  "employmentType": "FULL_TIME",
  "baseSalary": 50000
}
```

### Leave

- `POST /api/v1/leave`
- `GET /api/v1/leave/my`
- `GET /api/v1/leave/pending`
- `PATCH /api/v1/leave/{id}/approve`
- `PATCH /api/v1/leave/{id}/reject`
- `GET /api/v1/leave/balance/{employeeId}`

### Attendance

- `POST /api/v1/attendance/clock-in`
- `POST /api/v1/attendance/clock-out`
- `GET /api/v1/attendance/employees`

### Payroll

- `POST /api/v1/payroll/process`
- `GET /api/v1/payroll/employee/{employeeId}/month?month={m}&year={y}`
- `GET /api/v1/payroll/employee/{employeeId}`
- `GET /api/v1/payroll/{uuid}`

### Performance

- `POST /api/v1/performance`
- `GET /api/v1/performance/{id}`

## Security Rules (Current)

Configured in `src/main/java/com/example/hr_managment_system/security/SecurityConfig.java`.

- `POST /api/v1/auth/login` is public.
- Employee management is mostly admin-only.
- Payroll, attendance, and performance are protected by role rules.
- Any route not explicitly listed requires authentication.

## Known Gaps / Next Steps

- Add persistent refresh-token store and true logout revocation.
- Add controller/service tests for new auth and leave flows.
- Expand role rules for leave endpoints in `SecurityConfig` if needed.
- Harden password policy (length/complexity checks).
