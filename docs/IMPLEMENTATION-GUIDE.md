# Implementation Guide: Missing High-Priority Endpoints

## Overview
This guide provides step-by-step instructions for implementing the missing high-priority endpoints needed for a fully functional HR Management System.

---

## 1. Leave Approval Endpoint (HIGH PRIORITY)

### Current Status
- ❌ Not implemented
- ✅ Mentioned in SecurityConfig but no implementation
- ❌ Blocks HR/Manager leave management functionality

### What Needs to be Implemented

#### A. Update LeaveService Interface
```java
// Add to LeaveService.java
LeaveResponse approveLeave(String leaveId, String remarks);
LeaveResponse rejectLeave(String leaveId, String remarks);
```

#### B. Implement in LeaveServiceImpl
```java
@Override
@Transactional
public LeaveResponse approveLeave(String leaveId, String remarks) {
    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave request not found"));
    
    leave.setStatus(LeaveStatus.APPROVED);
    leave.setRemarks(remarks);
    leave.setApprovedAt(LocalDateTime.now());
    
    LeaveRequest updatedLeave = leaveRepository.save(leave);
    return leaveMapper.leaveToLeaveResponse(updatedLeave);
}

@Override
@Transactional
public LeaveResponse rejectLeave(String leaveId, String remarks) {
    LeaveRequest leave = leaveRepository.findById(leaveId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave request not found"));
    
    leave.setStatus(LeaveStatus.REJECTED);
    leave.setRemarks(remarks);
    leave.setApprovedAt(LocalDateTime.now());
    
    LeaveRequest updatedLeave = leaveRepository.save(leave);
    return leaveMapper.leaveToLeaveResponse(updatedLeave);
}
```

#### C. Update LeaveController
```java
@PatchMapping("/{id}/approve")
@ResponseStatus(HttpStatus.OK)
public LeaveResponse approveLeave(
    @PathVariable String id, 
    @RequestBody LeaveApprovalRequest request) {
    return leaveService.approveLeave(id, request.remarks());
}

@PatchMapping("/{id}/reject")
@ResponseStatus(HttpStatus.OK)
public LeaveResponse rejectLeave(
    @PathVariable String id, 
    @RequestBody LeaveApprovalRequest request) {
    return leaveService.rejectLeave(id, request.remarks());
}
```

#### D. Create DTO
```java
// LeaveApprovalRequest.java
public record LeaveApprovalRequest(
    String remarks
) {}
```

#### E. Update LeaveRequest Entity
```java
// Add fields to LeaveRequest entity
private LeaveStatus status; // PENDING, APPROVED, REJECTED, CANCELLED
private String remarks;
private LocalDateTime approvedAt;
```

---

## 2. Department Management Endpoints (HIGH PRIORITY)

### Current Status
- ❌ No controller exists
- ✅ Domain model exists
- ✅ Repository exists
- ❌ Service layer incomplete

### What Needs to be Implemented

#### A. Create DepartmentService Interface
```java
// DepartmentService.java
public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse getDepartmentById(String id);
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(String id, DepartmentRequest request);
    void deleteDepartment(String id);
}
```

#### B. Implement DepartmentServiceImpl
```java
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    
    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
            .map(departmentMapper::departmentToDepartmentResponse)
            .toList();
    }
    
    @Override
    public DepartmentResponse getDepartmentById(String id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        return departmentMapper.departmentToDepartmentResponse(department);
    }
    
    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department name already exists");
        }
        
        Department department = departmentMapper.departmentRequestToDepartment(request);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.departmentToDepartmentResponse(savedDepartment);
    }
    
    @Override
    public DepartmentResponse updateDepartment(String id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        
        department.setName(request.name());
        department.setDescription(request.description());
        
        Department updatedDepartment = departmentRepository.save(department);
        return departmentMapper.departmentToDepartmentResponse(updatedDepartment);
    }
    
    @Override
    public void deleteDepartment(String id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found");
        }
        departmentRepository.deleteById(id);
    }
}
```

#### C. Create DepartmentController
```java
@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentService.getAllDepartments();
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResponse getDepartmentById(@PathVariable String id) {
        return departmentService.getDepartmentById(id);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse createDepartment(@RequestBody DepartmentRequest request) {
        return departmentService.createDepartment(request);
    }
    
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResponse updateDepartment(
        @PathVariable String id, 
        @RequestBody DepartmentRequest request) {
        return departmentService.updateDepartment(id, request);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable String id) {
        departmentService.deleteDepartment(id);
    }
}
```

#### D. Create DTOs
```java
// DepartmentRequest.java
public record DepartmentRequest(
    String name,
    String description
) {}

// DepartmentResponse.java
public record DepartmentResponse(
    String id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

#### E. Update SecurityConfig
```java
// Add to SecurityConfig.java filterChain method
.requestMatchers(HttpMethod.POST, "/api/v1/department").hasRole("ADMIN")
.requestMatchers(HttpMethod.PUT, "/api/v1/department/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.DELETE, "/api/v1/department/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.GET, "/api/v1/department/**").hasAnyRole("ADMIN", "MANAGER")
```

---

## 3. Role Management Endpoints (HIGH PRIORITY)

### Current Status
- ❌ No controller exists
- ✅ Domain model exists
- ✅ Repository exists
- ❌ Service layer incomplete

### What Needs to be Implemented

#### A. Create RoleService Interface
```java
// RoleService.java
public interface RoleService {
    List<RoleResponse> getAllRoles();
    RoleResponse getRoleById(String id);
    RoleResponse createRole(RoleRequest request);
    RoleResponse updateRole(String id, RoleRequest request);
    void deleteRole(String id);
}
```

#### B. Implement RoleServiceImpl
```java
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    
    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
            .map(roleMapper::roleToRoleResponse)
            .toList();
    }
    
    @Override
    public RoleResponse getRoleById(String id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        return roleMapper.roleToRoleResponse(role);
    }
    
    @Override
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role name already exists");
        }
        
        Role role = roleMapper.roleRequestToRole(request);
        Role savedRole = roleRepository.save(role);
        return roleMapper.roleToRoleResponse(savedRole);
    }
    
    @Override
    public RoleResponse updateRole(String id, RoleRequest request) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        
        role.setName(request.name());
        role.setDescription(request.description());
        
        Role updatedRole = roleRepository.save(role);
        return roleMapper.roleToRoleResponse(updatedRole);
    }
    
    @Override
    public void deleteRole(String id) {
        if (!roleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found");
        }
        roleRepository.deleteById(id);
    }
}
```

#### C. Create RoleController
```java
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {
    
    private final RoleService roleService;
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleResponse getRoleById(@PathVariable String id) {
        return roleService.getRoleById(id);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse createRole(@RequestBody RoleRequest request) {
        return roleService.createRole(request);
    }
    
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleResponse updateRole(
        @PathVariable String id, 
        @RequestBody RoleRequest request) {
        return roleService.updateRole(id, request);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
    }
}
```

#### D. Create DTOs
```java
// RoleRequest.java
public record RoleRequest(
    String name,
    String description
) {}

// RoleResponse.java
public record RoleResponse(
    String id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

#### E. Update SecurityConfig
```java
// Add to SecurityConfig.java filterChain method
.requestMatchers(HttpMethod.POST, "/api/v1/role").hasRole("ADMIN")
.requestMatchers(HttpMethod.PUT, "/api/v1/role/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.DELETE, "/api/v1/role/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.GET, "/api/v1/role/**").hasAnyRole("ADMIN", "MANAGER")
```

---

## 4. Enhanced Employee Search/Filter (HIGH PRIORITY)

### Current Status
- ✅ Basic isActive filter exists
- ❌ No department filter
- ❌ No role filter
- ❌ No search by name/email

### Update EmployeeController
```java
@GetMapping
@ResponseStatus(HttpStatus.OK)
public List<EmployeeResponse> getAllEmployees(
    @RequestParam(required = false) Boolean isActive,
    @RequestParam(required = false) String departmentId,
    @RequestParam(required = false) String roleId,
    @RequestParam(required = false) String search) {
    return employeeService.getAllEmployees(isActive, departmentId, roleId, search);
}
```

### Update EmployeeService Interface & Implementation
```java
// Add method to service
List<EmployeeResponse> getAllEmployees(Boolean isActive, String departmentId, String roleId, String search);

// Implementation
@Override
public List<EmployeeResponse> getAllEmployees(Boolean isActive, String departmentId, String roleId, String search) {
    return employeeRepository.findAll().stream()
        .filter(e -> isActive == null || e.getIsActive() == isActive)
        .filter(e -> departmentId == null || e.getDepartment().getId().equals(departmentId))
        .filter(e -> roleId == null || e.getRole().getId().equals(roleId))
        .filter(e -> search == null || search.isEmpty() || 
            e.getFirstName().toLowerCase().contains(search.toLowerCase()) ||
            e.getLastName().toLowerCase().contains(search.toLowerCase()) ||
            e.getEmail().toLowerCase().contains(search.toLowerCase()) ||
            e.getUsername().toLowerCase().contains(search.toLowerCase()))
        .map(employeeMapper::employeeToEmployeeResponse)
        .toList();
}
```

---

## Implementation Priority Timeline

### Week 1:
1. Implement Leave Approval Endpoints
2. Create Department Management Endpoints

### Week 2:
1. Create Role Management Endpoints
2. Enhance Employee Search/Filter

### Week 3:
1. Testing and bug fixes
2. Update frontend documentation

---

## Testing the New Endpoints

### Sample Test Data
```bash
# Create Department
curl -X POST http://localhost:8080/api/v1/department \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"IT","description":"Information Technology"}'

# Create Role
curl -X POST http://localhost:8080/api/v1/role \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Developer","description":"Software Developer"}'

# Search Employees by name
curl -X GET "http://localhost:8080/api/v1/employee?search=john&departmentId=dept-001" \
  -H "Authorization: Bearer MANAGER_TOKEN"

# Approve Leave
curl -X PATCH http://localhost:8080/api/v1/leave/leave-001/approve \
  -H "Authorization: Bearer MANAGER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"remarks":"Approved"}'
```

---

## Notes
- Always add proper validation in DTOs
- Use @Transactional for operations that modify data
- Implement proper error handling
- Add unit tests for each new service method
- Update Swagger documentation

