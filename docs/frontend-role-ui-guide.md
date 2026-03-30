# Frontend Role UI Guide (Admin, Employee, HR)

This guide helps frontend developers build role-based UI using the current backend API.

> Role mapping: **HR UI = `MANAGER` role** in backend.

## 1) Quick Start

- Base URL: `http://localhost:9090`
- Auth header for protected APIs:

```text
Authorization: Bearer <accessToken>
```

- Login endpoint (public): `POST /api/v1/auth/login`
- Role source for UI gating: `GET /api/v1/auth/me`

## 2) Auth Flow for UI

### Login

`POST /api/v1/auth/login`

Request:

```json
{
  "username": "admin.user",
  "password": "admin123"
}
```

Response:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer"
}
```

### Get current user and role

`GET /api/v1/auth/me`

Response:

```json
{
  "employeeId": "...",
  "username": "...",
  "email": "...",
  "role": "ADMIN"
}
```

Use `role` to decide which menu/screens to show.

## 3) Role-to-Screen Mapping

### ADMIN

Show:
- Employee management (list/create/update/delete)
- Admin attendance dashboard (global)
- HR attendance report screens
- Payroll processing
- Performance create + view
- Leave monitoring/approval screens

### HR (MANAGER)

Show:
- Employee list/detail (read-only from current security)
- Department attendance report screen
- Team leave approval screens
- Payroll view + payroll processing
- Performance create + view

Hide:
- Employee create/update/delete and role/status management
- Admin-only attendance dashboard endpoint

### EMPLOYEE

Show:
- My attendance
- Clock in / clock out
- My payroll and payroll detail views (if employeeId known)
- My performance review detail
- My leave requests + create leave request

Hide:
- Employee management screens
- Payroll process screen
- Performance create screen
- HR/Admin attendance reports

## 4) Endpoint Access Matrix (Current Backend Rules)

| Domain | Endpoint | ADMIN | HR (MANAGER) | EMPLOYEE |
|---|---|---:|---:|---:|
| Auth | `POST /api/v1/auth/login` | Yes | Yes | Yes |
| Auth | `GET /api/v1/auth/me` | Yes | Yes | Yes |
| Employee | `GET /api/v1/employee/**` | Yes | Yes | No |
| Employee | `POST/PUT/DELETE /api/v1/employee/**` | Yes | No | No |
| Attendance | `POST /api/v1/attendance/clock-in` | Yes | Yes | Yes |
| Attendance | `POST /api/v1/attendance/clock-out` | Yes | Yes | Yes |
| Attendance | `GET /api/v1/attendance/my` | Yes | Yes | Yes |
| Attendance | `GET /api/v1/attendance/hr` | Yes | Yes | No |
| Attendance | `GET /api/v1/attendance/admin` | Yes | No | No |
| Attendance | `GET /api/v1/attendance/employees` | Yes | Yes | No |
| Payroll | `POST /api/v1/payroll/process` | Yes | Yes | No |
| Payroll | `GET /api/v1/payroll/**` | Yes | Yes | Yes |
| Performance | `POST /api/v1/performance/**` | Yes | Yes | No |
| Performance | `GET /api/v1/performance/**` | Yes | Yes | Yes |
| Leave* | `POST/GET/PATCH /api/v1/leave/**` | Yes | Yes | Yes |

`Leave*` note: current security config does not explicitly role-restrict leave routes, so all authenticated users can access them. UI should still hide approve/reject from employees.

## 5) Attendance APIs for New UI

### Employee attendance page

- `GET /api/v1/attendance/my?from=2026-03-01&to=2026-03-30`

### HR attendance report page

- `GET /api/v1/attendance/hr?departmentId=<id>&month=3&year=2026`

### Admin attendance dashboard page

- `GET /api/v1/attendance/admin?isActive=true&from=2026-03-01&to=2026-03-30`

## 6) Suggested Frontend Routes

- `/login`
- `/dashboard`
- `/attendance/my`
- `/attendance/hr-report` (HR + Admin)
- `/attendance/admin` (Admin only)
- `/employees` (HR + Admin)
- `/employees/manage` (Admin only)
- `/leave/my`
- `/leave/pending` (HR + Admin)
- `/payroll/my`
- `/payroll/process` (HR + Admin)
- `/performance/my`
- `/performance/create` (HR + Admin)

## 7) Minimal Role Guard Example (TypeScript)

```ts
export type AppRole = "ADMIN" | "MANAGER" | "EMPLOYEE";

export function canAccess(role: AppRole, routeKey: string): boolean {
  const acl: Record<string, AppRole[]> = {
    attendanceMy: ["ADMIN", "MANAGER", "EMPLOYEE"],
    attendanceHr: ["ADMIN", "MANAGER"],
    attendanceAdmin: ["ADMIN"],
    employeeRead: ["ADMIN", "MANAGER"],
    employeeManage: ["ADMIN"],
    payrollProcess: ["ADMIN", "MANAGER"]
  };

  return acl[routeKey]?.includes(role) ?? false;
}
```

## 8) UI Implementation Tips

- Call `/api/v1/auth/me` immediately after login and on app reload.
- Keep JWT in secure storage strategy used by your app (prefer HttpOnly cookie if backend supports it later).
- Build menu from role map (not hardcoded per page).
- Handle `401` by redirecting to login and clearing token.
- Handle `403` with an Access Denied page.
- For HR label in UI, display `HR` but treat role value `MANAGER` as HR.

