#!/usr/bin/env fish

set -e

set -q BASE_URL; or set BASE_URL "http://localhost:9090"
set -q ADMIN_USERNAME; or set ADMIN_USERNAME ""
set -q ADMIN_PASSWORD; or set ADMIN_PASSWORD ""
set -q EMPLOYEE_USERNAME; or set EMPLOYEE_USERNAME ""
set -q EMPLOYEE_PASSWORD; or set EMPLOYEE_PASSWORD ""

set -q TEST_EMPLOYEE_ID; or set TEST_EMPLOYEE_ID ""
set -q TEST_DEPARTMENT_ID; or set TEST_DEPARTMENT_ID ""
set -q TEST_ROLE_ID; or set TEST_ROLE_ID ""
set -q TEST_PAYROLL_ID; or set TEST_PAYROLL_ID ""
set -q TEST_PERFORMANCE_ID; or set TEST_PERFORMANCE_ID ""
set -q TEST_LEAVE_ID; or set TEST_LEAVE_ID ""

function fail
    echo "[FAIL] $argv[1]"
    exit 1
end

function pass
    echo "[PASS] $argv[1]"
end

function request_status
    set -l method $argv[1]
    set -l path $argv[2]
    set -l token $argv[3]
    set -l body $argv[4]

    if test -n "$token"
        if test -n "$body"
            curl -s -o /dev/null -w "%{http_code}" -X $method "$BASE_URL$path" \
                -H "Authorization: Bearer $token" \
                -H "Content-Type: application/json" \
                -d "$body"
        else
            curl -s -o /dev/null -w "%{http_code}" -X $method "$BASE_URL$path" \
                -H "Authorization: Bearer $token"
        end
    else
        if test -n "$body"
            curl -s -o /dev/null -w "%{http_code}" -X $method "$BASE_URL$path" \
                -H "Content-Type: application/json" \
                -d "$body"
        else
            curl -s -o /dev/null -w "%{http_code}" -X $method "$BASE_URL$path"
        end
    end
end

function expect_status
    set -l name $argv[1]
    set -l method $argv[2]
    set -l path $argv[3]
    set -l token $argv[4]
    set -l expected $argv[5]
    set -l body $argv[6]

    set -l got (request_status $method $path $token "$body")
    if test "$got" = "$expected"
        pass "$name ($method $path -> $got)"
    else
        fail "$name ($method $path expected $expected got $got)"
    end
end

function expect_status_in
    set -l name $argv[1]
    set -l method $argv[2]
    set -l path $argv[3]
    set -l token $argv[4]
    set -l allowed_csv $argv[5]
    set -l body $argv[6]

    set -l got (request_status $method $path $token "$body")
    set -l allowed (string split "," $allowed_csv)
    if contains -- $got $allowed
        pass "$name ($method $path -> $got)"
    else
        fail "$name ($method $path expected one of [$allowed_csv] got $got)"
    end
end

function login_and_get_token
    set -l username $argv[1]
    set -l password $argv[2]

    if test -z "$username" -o -z "$password"
        echo ""
        return 0
    end

    set -l response (curl -s -X POST "$BASE_URL/api/v1/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\",\"password\":\"$password\"}")

    set -l token (echo $response | jq -r '.accessToken // empty')
    echo $token
end

if not type -q jq
    fail "jq is required (install jq first)"
end

echo "== Endpoint smoke test started =="
echo "BASE_URL=$BASE_URL"

# Public auth endpoint should be reachable (200 for valid or 401/403 for invalid credentials).
set -l login_probe (request_status POST "/api/v1/auth/login" "" '{"username":"x","password":"x"}')
if contains -- $login_probe 200 401 403
    pass "Auth login endpoint reachable"
else
    fail "Auth login endpoint unreachable (status $login_probe)"
end

set -l admin_token (login_and_get_token "$ADMIN_USERNAME" "$ADMIN_PASSWORD")
if test -z "$admin_token"
    echo "[WARN] ADMIN credentials not provided or login failed; running auth-only checks."
else
    pass "Admin login success"
end

set -l employee_token (login_and_get_token "$EMPLOYEE_USERNAME" "$EMPLOYEE_PASSWORD")
if test -n "$EMPLOYEE_USERNAME" -a -n "$EMPLOYEE_PASSWORD"
    if test -n "$employee_token"
        pass "Employee login success"
    else
        echo "[WARN] Employee login failed; employee-role checks will be skipped."
    end
end

# Auth endpoints
if test -n "$admin_token"
    expect_status "Auth me" GET "/api/v1/auth/me" $admin_token 200 ""
    expect_status "Auth refresh" POST "/api/v1/auth/refresh" $admin_token 200 "{\"token\":\"$admin_token\"}"
    expect_status "Change password validation" POST "/api/v1/auth/change-password" $admin_token 400 "{}"
end

# Endpoint access checks by role (status may vary by your current data/state).
if test -n "$admin_token"
    expect_status "Employee list (admin)" GET "/api/v1/employee" $admin_token 200 ""
    expect_status "Leave pending (admin/manager)" GET "/api/v1/leave/pending" $admin_token 200 ""
    expect_status "Attendance list (admin/manager)" GET "/api/v1/attendance/employees" $admin_token 200 ""
end

if test -n "$employee_token"
    expect_status "Employee cannot list employees" GET "/api/v1/employee" $employee_token 403 ""
    if test -n "$TEST_EMPLOYEE_ID"
        expect_status_in "Employee payroll route reachable" GET "/api/v1/payroll/employee/$TEST_EMPLOYEE_ID" $employee_token "200,404" ""
    end
    if test -n "$TEST_PERFORMANCE_ID"
        expect_status_in "Employee performance route reachable" GET "/api/v1/performance/$TEST_PERFORMANCE_ID" $employee_token "200,404" ""
    end
end

# Optional ID-based endpoint probes
if test -n "$admin_token" -a -n "$TEST_EMPLOYEE_ID"
    expect_status_in "Employee by id" GET "/api/v1/employee/$TEST_EMPLOYEE_ID" $admin_token "200,404" ""
    expect_status "Employee status patch validation" PATCH "/api/v1/employee/$TEST_EMPLOYEE_ID/status" $admin_token 400 "{}"
end

if test -n "$admin_token" -a -n "$TEST_PAYROLL_ID"
    expect_status_in "Payroll by id" GET "/api/v1/payroll/$TEST_PAYROLL_ID" $admin_token "200,404" ""
end

if test -n "$admin_token" -a -n "$TEST_PERFORMANCE_ID"
    expect_status_in "Performance by id" GET "/api/v1/performance/$TEST_PERFORMANCE_ID" $admin_token "200,404" ""
end

if test -n "$admin_token" -a -n "$TEST_LEAVE_ID"
    expect_status_in "Leave approve" PATCH "/api/v1/leave/$TEST_LEAVE_ID/approve" $admin_token "200,404" ""
end

echo "== Smoke test finished =="
