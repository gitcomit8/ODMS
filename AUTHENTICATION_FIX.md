# Authentication Issue Fix - UserDetails Null

## Problem

Error encountered:
`Cannot invoke "org.springframework.security.core.userdetails.UserDetails.getAuthorities()" because "approver" is null`

This error occurs when trying to approve or reject requests without being properly authenticated.

## Root Cause

The application has security configured with `.anyRequest().permitAll()` for development purposes, which allows all
requests to proceed without authentication. This means:

1. Users can access protected pages without logging in
2. The `@AuthenticationPrincipal UserDetails` parameter is `null`
3. When the service tries to access `approver.getAuthorities()`, it throws a `NullPointerException`

## Solution Implemented

### 1. Added Null Checks in Service Layer

**File:** `EventRequestService.java`

Both `approveRequest()` and `rejectRequest()` methods now check for null UserDetails:

```java
if (approver == null) {
    throw new IllegalStateException("User not authenticated. Please log in to approve requests.");
}
```

This prevents NullPointerException and provides a clear error message.

### 2. Fixed Dev Login Session Persistence

**File:** `DevLoginController.java`

The dev login wasn't persisting authentication across requests. Fixed by:

```java
// Create a new security context and set the authentication
SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
securityContext.setAuthentication(auth);
SecurityContextHolder.setContext(securityContext);

// Persist the security context in the session
request.getSession().setAttribute(
    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
    securityContext
);
```

### 3. Added Authentication Checks in Controllers

**Files:**

- `ApproverDashboardController.java`
- `EventRequestController.java`

Both approve and reject endpoints now check if user is authenticated:

```java
if (userDetails == null) {
    redirectAttributes.addFlashAttribute("errorMessage", 
        "You must be logged in to approve requests. Please log in first.");
    return "redirect:/dev-login";
}
```

## How to Use Dev Login

### Step 1: Navigate to Dev Login

Visit: `http://localhost:8080/dev-login`

### Step 2: Choose Your Role

Select one of the following roles:

- **ROLE_EVENT_COORDINATOR** - Faculty Coordinator (approves SUBMITTED requests)
- **ROLE_STUDENT_WELFARE** - Student Welfare (approves PENDING_WELFARE_APPROVAL)
- **ROLE_HOD** - Head of Department (approves PENDING_HOD_APPROVAL)
- **ROLE_ADMIN** - Administrator (can act at any stage)
- **ROLE_FACULTY** - Faculty member
- **ROLE_STUDENT_ORGANIZER** - Student organizer

### Step 3: Enter Username

Enter any username (e.g., "TestUser", "coordinator@test.com")

### Step 4: Login

Click the "Login" button

The system will:

1. Create an authentication session with your chosen role
2. Redirect you to the appropriate dashboard
3. Persist your session across requests

## Testing the Fix

### Test Case 1: Approve Without Login ✓

1. Try to access `/approver/dashboard` directly
2. Try to approve a request
3. **Expected:** Redirected to `/dev-login` with error message

### Test Case 2: Approve With Login ✓

1. Login via `/dev-login` with `ROLE_EVENT_COORDINATOR`
2. Navigate to `/approver/dashboard`
3. Approve a SUBMITTED request
4. **Expected:** Success message, request moves to PENDING_WELFARE_APPROVAL

### Test Case 3: Session Persistence ✓

1. Login via `/dev-login`
2. Navigate between pages
3. Perform approve/reject actions
4. **Expected:** Authentication persists, actions succeed

## Error Messages Explained

### "You must be logged in to approve requests. Please log in first."

**Cause:** Trying to approve/reject without being authenticated.
**Solution:** Visit `/dev-login` and log in with appropriate role.

### "User not authenticated. Please log in to approve requests."

**Cause:** UserDetails is null in the service layer.
**Solution:** Ensure you're logged in via `/dev-login`.

### "User role not found. Please ensure you have a valid role assigned."

**Cause:** Authenticated but no role assigned to the user.
**Solution:** Re-login via `/dev-login` and select a valid role.

## Files Modified

```
src/main/java/in/srmup/odms/
├── controller/
│   ├── ApproverDashboardController.java   [MODIFIED - Added auth check]
│   ├── EventRequestController.java        [MODIFIED - Added auth check]
│   └── DevLoginController.java            [MODIFIED - Fixed session persistence]
└── service/
    └── EventRequestService.java           [MODIFIED - Added null checks]

Documentation:
└── AUTHENTICATION_FIX.md                  [CREATED - This file]
```

## Production Considerations

⚠️ **Important:** The current security configuration is FOR DEVELOPMENT ONLY!

In production, you should:

1. **Remove `.anyRequest().permitAll()`**
   ```java
   .authorizeHttpRequests(authz -> authz
       .requestMatchers("/public/**").permitAll()
       .anyRequest().authenticated()  // Require authentication
   )
   ```

2. **Disable dev-login**
   Set in `application.properties`:
   ```properties
   spring.profiles.active=prod
   app.security.dev-login.enabled=false
   ```

3. **Implement proper authentication**
    - Use OTP-based login (already implemented in the system)
    - Or integrate with organizational SSO/OAuth2

## Session Configuration

The fix relies on HTTP sessions. Ensure your application properties include:

```properties
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=false  # Set to true in production with HTTPS
```

## Debugging Authentication Issues

If you encounter authentication problems:

1. **Check if logged in:**
   ```java
   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
   System.out.println("Authenticated: " + (auth != null && auth.isAuthenticated()));
   System.out.println("Principal: " + (auth != null ? auth.getPrincipal() : "null"));
   ```

2. **Check session:**
   ```java
   HttpSession session = request.getSession(false);
   System.out.println("Session ID: " + (session != null ? session.getId() : "null"));
   ```

3. **Enable security logging:**
   In `application.properties`:
   ```properties
   logging.level.org.springframework.security=DEBUG
   ```

## Quick Start

1. Start the application
2. Navigate to `http://localhost:8080/dev-login`
3. Select role: `ROLE_EVENT_COORDINATOR`
4. Enter username: `coordinator@test.com`
5. Click Login
6. You'll be redirected to `/approver/dashboard`
7. You can now approve/reject requests

## Verification

To verify the fix is working:

```bash
# Check that dev login is enabled
grep "app.security.dev-login.enabled" src/main/resources/application.properties

# Should output: app.security.dev-login.enabled=true
```

## Summary

✅ Null checks added to prevent NullPointerException  
✅ Dev login session persistence fixed  
✅ Authentication checks in controllers  
✅ Clear error messages for unauthenticated users  
✅ Automatic redirect to login page when not authenticated  
✅ Proper session management for dev authentication  
✅ Compilation successful with no errors

The authentication issue is now resolved, and users must log in via `/dev-login` before they can approve or reject
requests.
