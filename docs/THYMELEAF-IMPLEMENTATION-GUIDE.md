# Thymeleaf UI Frontend Implementation Guide

## Overview
This guide provides complete instructions for building the Thymeleaf UI for the HR Management System with role-based dashboards and CRUD operations.

---

## Table of Contents
1. [Project Setup](#project-setup)
2. [Architecture](#architecture)
3. [View Controllers](#view-controllers)
4. [Thymeleaf Templates](#thymeleaf-templates)
5. [Authentication & Security](#authentication--security)
6. [API Integration](#api-integration)
7. [Styling & Assets](#styling--assets)
8. [Testing](#testing)
9. [Deployment](#deployment)

---

## Project Setup

### Step 1: Dependencies (build.gradle)

```groovy
dependencies {
    // Web & View
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    
    // Security
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.7'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.7'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.7'
    
    // WebJars for Bootstrap & jQuery
    implementation 'org.webjars:bootstrap:5.3.0'
    implementation 'org.webjars:jquery:3.6.0'
    implementation 'org.webjars:popper.js:2.11.6'
    implementation 'org.webjars:font-awesome:6.4.0'
    
    // Data & Mapping
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.mapstruct:mapstruct:1.6.3'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.6.3'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.mockito:mockito-core:5.2.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.2.0'
}
```

### Step 2: Application Properties

```properties
# server
server.port=9090

# Thymeleaf Configuration
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8
spring.thymeleaf.cache=false

# MVC
spring.mvc.static-path-pattern=/static/**

# Security
app.security.jwt.secret=change-this-secret-key-to-at-least-32-chars
app.security.jwt.expiration-ms=86400000
```

---

## Architecture

### Directory Structure

```
src/main/resources/
├── templates/
│   ├── auth/
│   │   ├── login.html
│   │   └── logout.html
│   ├── layout/
│   │   ├── base.html (Master template)
│   │   ├── navbar.html
│   │   └── sidebar.html
│   ├── dashboard/
│   │   ├── admin-dashboard.html
│   │   ├── hr-dashboard.html
│   │   └── employee-dashboard.html
│   ├── employee/
│   │   ├── list.html
│   │   ├── create.html
│   │   ├── edit.html
│   │   ├── detail.html
│   │   └── view.html
│   ├── attendance/
│   │   ├── clock-inout.html
│   │   ├── my-attendance.html
│   │   ├── employee-attendance.html
│   │   ├── hr-report.html
│   │   └── admin-report.html
│   ├── leave/
│   │   ├── request.html
│   │   ├── my-leaves.html
│   │   ├── pending.html
│   │   ├── balance.html
│   │   └── detail.html
│   ├── payroll/
│   │   ├── my-payroll.html
│   │   ├── payroll-detail.html
│   │   ├── process.html
│   │   └── reports.html
│   ├── performance/
│   │   ├── my-reviews.html
│   │   ├── create.html
│   │   ├── reviews.html
│   │   └── detail.html
│   └── errors/
│       ├── 403.html
│       ├── 404.html
│       └── 500.html
├── static/
│   ├── css/
│   │   └── custom.css
│   ├── js/
│   │   ├── api.js
│   │   └── app.js
│   └── images/
│       └── logo.png
```

---

## View Controllers

### 1. AuthViewController

```java
@Controller
public class AuthViewController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/logout-page")
    public String logoutPage() {
        return "auth/logout";
    }
}
```

### 2. AdminDashboardViewController

**Endpoints:**
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/employees` - List all employees
- `GET /admin/employees/create` - Create employee form
- `GET /admin/employees/{id}` - View employee details
- `GET /admin/employees/{id}/edit` - Edit employee form
- `GET /admin/leaves/pending` - Pending leave requests
- `GET /admin/attendance` - Attendance reports
- `GET /admin/payroll` - Payroll management
- `GET /admin/payroll/process` - Process payroll form

### 3. HRDashboardViewController

**Endpoints:**
- `GET /hr/dashboard` - HR dashboard
- `GET /hr/leaves/pending` - Pending leaves
- `GET /hr/leaves/{id}` - Leave details
- `GET /hr/attendance` - Attendance report for department

### 4. EmployeeDashboardViewController

**Endpoints:**
- `GET /employee/dashboard` - Employee dashboard
- `GET /employee/profile` - My profile
- `GET /employee/attendance` - My attendance
- `GET /employee/attendance/clock-inout` - Clock in/out page
- `GET /employee/leaves` - My leaves
- `GET /employee/leaves/request` - Request leave form
- `GET /employee/leaves/balance` - Leave balance
- `GET /employee/payroll` - My payroll
- `GET /employee/performance` - My performance reviews

---

## Thymeleaf Templates

### Login Template (auth/login.html)

**Features:**
- Responsive login form
- Demo credentials display
- Error/Success messages
- Gradient background

**Key Elements:**
```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username" required />
    <input type="password" name="password" required />
    <button type="submit">Login</button>
</form>
```

### Dashboard Templates

#### Admin Dashboard (dashboard/admin-dashboard.html)

**Displays:**
- Total employees count
- Pending leaves count
- Present today count
- Monthly payroll status
- Quick actions (Add employee, Process payroll)
- Recent employees table

**AJAX Calls:**
```javascript
GET /api/v1/leave/pending
GET /api/v1/attendance/admin?from=TODAY&to=TODAY
GET /api/v1/employee?isActive=true
```

#### HR Dashboard (dashboard/hr-dashboard.html)

**Displays:**
- Pending leaves count
- Approved this month
- Rejected this month
- Pending leave requests table with approve/reject buttons
- Quick actions

**AJAX Calls:**
```javascript
GET /api/v1/leave/pending
PATCH /api/v1/leave/{id}/approve
PATCH /api/v1/leave/{id}/reject
```

#### Employee Dashboard (dashboard/employee-dashboard.html)

**Displays:**
- Welcome message
- Clock in/out buttons
- Days present this month
- Leave balance
- Hours worked this month
- Recent attendance table
- Recent leaves table

**AJAX Calls:**
```javascript
POST /api/v1/attendance/clock-in
POST /api/v1/attendance/clock-out
GET /api/v1/attendance/my
GET /api/v1/leave/my
GET /api/v1/leave/balance/{employeeId}
```

---

## Authentication & Security

### Security Configuration

Update `SecurityConfig.java`:

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/webjars/**", "/static/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/hr/**").hasRole("MANAGER")
                .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Login Flow

1. User submits `/login` form
2. Spring Security authenticates credentials
3. On success: Redirect to role-specific dashboard
4. On failure: Redirect to `/login?error`
5. JWT token stored in localStorage for API calls

---

## API Integration

### API Helper (JavaScript)

```javascript
const API = {
    async get(url) {
        return fetch(url, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
            }
        }).then(res => res.json());
    },

    async post(url, data) {
        return fetch(url, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(res => res.json());
    },

    async put(url, data) {
        return fetch(url, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(res => res.json());
    },

    async patch(url, data) {
        return fetch(url, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(res => res.json());
    },

    async delete(url) {
        return fetch(url, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json'
            }
        }).then(res => res.ok);
    }
};
```

### Example: Approve Leave

```javascript
function approveLeave(leaveId, remarks) {
    API.patch(`/api/v1/leave/${leaveId}/approve`, { remarks })
        .then(data => {
            showNotification('Leave approved successfully');
            location.reload();
        })
        .catch(err => showNotification('Error approving leave', 'danger'));
}
```

---

## Styling & Assets

### Bootstrap Integration

All templates use Bootstrap 5 via WebJars:

```html
<link rel="stylesheet" th:href="@{/webjars/bootstrap/5.3.0/css/bootstrap.min.css}">
<link rel="stylesheet" th:href="@{/webjars/font-awesome/6.4.0/css/all.min.css}">
```

### Custom CSS Variables

```css
:root {
    --primary-color: #2c3e50;
    --secondary-color: #3498db;
    --success-color: #27ae60;
    --danger-color: #e74c3c;
    --warning-color: #f39c12;
}
```

### Status Badges

```html
<span class="badge bg-success" th:if="${item.status == 'ACTIVE'}">Active</span>
<span class="badge bg-warning" th:if="${item.status == 'PENDING'}">Pending</span>
<span class="badge bg-danger" th:if="${item.status == 'REJECTED'}">Rejected</span>
```

---

## Testing

### Integration Tests for Views

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ViewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAdminDashboardLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/admin-dashboard"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    public void testEmployeeDashboardAccessDeniedForNonEmployees() throws Exception {
        mockMvc.perform(get("/employee/dashboard")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }
}
```

---

## Deployment

### Docker Setup

```dockerfile
FROM openjdk:21-slim
WORKDIR /app
COPY build/libs/hr-managment-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Environment Variables

```bash
SERVER_PORT=9090
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=your-secret-key
THYMELEAF_CACHE=false
```

---

## Common Issues & Solutions

### Issue 1: CSRF Token Missing
**Solution:** Add CSRF token to forms:
```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
```

### Issue 2: JavaScript Files Not Loading
**Solution:** Use Thymeleaf path helper:
```html
<script th:src="@{/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js}"></script>
```

### Issue 3: Models Not Binding to View
**Solution:** Ensure controller passes model attribute:
```java
model.addAttribute("employee", employeeService.getEmployeeById(id));
```

---

## Best Practices

1. **Always include error handling** in views
2. **Use AJAX for data loading** to prevent full page reloads
3. **Implement loading spinners** for async operations
4. **Validate input** both client-side and server-side
5. **Use strong security headers** in SecurityConfig
6. **Cache static resources** in production
7. **Test role-based access** thoroughly
8. **Use Thymeleaf conditionals** for role-specific content

---

## References

- [Spring Boot Thymeleaf Documentation](https://spring.io/guides/gs/serving-web-content/)
- [Bootstrap 5 Documentation](https://getbootstrap.com/docs/5.0/)
- [Thymeleaf Expression Language](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
- [Spring Security Documentation](https://spring.io/projects/spring-security)

