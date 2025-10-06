# CSRF Protection Implementation

This document describes how CSRF (Cross-Site Request Forgery) protection is implemented consistently throughout the ODMS
application.

## Overview

CSRF protection is **enabled** throughout the application to prevent unauthorized commands from being transmitted from a
user that the web application trusts.

## Configuration

### Spring Security Configuration

Location: `src/main/java/in/srmup/odms/config/SecurityConfig.java`

CSRF is enabled by default in Spring Security with the following configuration:

```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/h2-console/**")  // Only H2 console is excluded for development
)
```

**Important**: CSRF is enabled for all endpoints except the H2 console (which is only for development).

## Implementation Details

### 1. HTML Forms with Thymeleaf

All forms using Thymeleaf's `th:action` attribute automatically include CSRF tokens. Example:

```html
<form method="post" th:action="@{/event-requests/submit}">
    <!-- Thymeleaf automatically adds a hidden CSRF token field here -->
    <!-- Other form fields -->
</form>
```

**No manual CSRF token addition is needed** for standard Thymeleaf forms.

### 2. AJAX Requests

For AJAX/fetch requests, CSRF tokens must be manually included in request headers.

#### Implementation Pattern

All HTML templates now include CSRF meta tags in the `<head>` section:

```html
<meta name="_csrf" th:content="${_csrf.token}"/>
<meta name="_csrf_header" th:content="${_csrf.headerName}"/>
```

JavaScript code retrieves and includes these tokens in fetch requests:

```javascript
// Get CSRF token from meta tags
const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// Include in fetch request
const response = await fetch('/api/endpoint', {
    method: 'GET',
    headers: {
        [header]: token
    }
});
```

#### Current AJAX Implementations

1. **Event Request Form** (`event-request-form.html`)
    - Location: Student detail fetching via `/api/students/{regNo}`
    - CSRF token is included in GET requests

## Files Updated

All HTML templates now include CSRF meta tags:

1. ✅ `admin-dashboard.html`
2. ✅ `admin-import.html`
3. ✅ `approver-dashboard.html`
4. ✅ `dev-login.html`
5. ✅ `error-page.html`
6. ✅ `event-details.html`
7. ✅ `event-request-form.html`
8. ✅ `faculty-dashboard.html`
9. ✅ `home.html`
10. ✅ `login.html`
11. ✅ `my-requests.html`
12. ✅ `pending-requests.html`
13. ✅ `success-page.html`

## Forms Inventory

The following forms are protected by CSRF tokens:

### Admin Forms

- `/admin/update-role` - User role update form
- `/admin/import/students` - Student CSV import form
- `/admin/import/faculty` - Faculty CSV import form

### Authentication Forms

- `/dev-login` - Developer login form
- `/generate-otp` - OTP generation form
- `/login-with-otp` - OTP login form

### Event Request Forms

- `/event-requests/submit` - New event request submission
- `/event-requests/approve/{id}` - Request approval
- `/event-requests/reject/{id}` - Request rejection
- `/approver/approve/{id}` - Approver dashboard approval
- `/approver/reject/{id}` - Approver dashboard rejection

## Testing CSRF Protection

### Verify CSRF is Enabled

1. Try to submit a form without a CSRF token - should get 403 Forbidden
2. Check browser DevTools Network tab - forms should include `_csrf` parameter
3. AJAX requests should include `X-CSRF-TOKEN` header (or configured header name)

### Expected Behavior

- ✅ Forms submitted with valid CSRF token: **SUCCESS**
- ❌ Forms submitted without CSRF token: **403 Forbidden**
- ❌ Forms with invalid/expired CSRF token: **403 Forbidden**

## Best Practices

1. **Always use Thymeleaf `th:action`** for forms - automatic CSRF token inclusion
2. **Include CSRF meta tags** in all pages that might use AJAX
3. **Retrieve tokens from meta tags** in JavaScript rather than hardcoding
4. **Never disable CSRF** in production environments
5. **Test form submissions** after any changes to ensure CSRF tokens are present

## Troubleshooting

### "403 Forbidden" on Form Submission

1. Check if CSRF meta tags are present in the HTML
2. Verify the form uses `th:action` instead of plain `action`
3. For AJAX, ensure CSRF token is included in headers
4. Check browser console for JavaScript errors

### AJAX Requests Failing

1. Verify CSRF meta tags exist in the page
2. Check that the token is being retrieved correctly
3. Ensure the header name matches the meta tag value
4. Verify the token is being sent in request headers

## Security Notes

- CSRF tokens are session-based and change with each session
- Tokens are automatically validated by Spring Security
- H2 console is excluded from CSRF for development only - **remove in production**
- All state-changing operations (POST, PUT, DELETE) require valid CSRF tokens
- GET requests do not require CSRF tokens but still receive them for consistency

## Future Enhancements

If you add new forms or AJAX endpoints:

1. Ensure HTML templates include CSRF meta tags
2. For Thymeleaf forms, use `th:action`
3. For AJAX requests, retrieve and include CSRF token in headers
4. Test the implementation before deployment

## Contact

For questions about CSRF implementation, refer to:

- Spring Security Documentation: https://docs.spring.io/spring-security/reference/features/exploits/csrf.html
- Thymeleaf + Spring Security: https://www.thymeleaf.org/doc/articles/springsecurity.html
