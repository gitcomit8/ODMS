package in.srmup.odms.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        System.out.println("User authorities upon login: " + authentication.getAuthorities());

        String redirectUrl = "/"; // Default redirect URL

        // Get the role of the logged-in user
        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        // Determine the redirect URL based on the role
        redirectUrl = switch (userRole) {
            case "ROLE_ADMIN" -> "/admin/dashboard";
            case "ROLE_EVENT_COORDINATOR", "ROLE_STUDENT_WELFARE", "ROLE_HOD" -> "/approver/dashboard";
            case "ROLE_STUDENT_ORGANIZER" -> "/event-requests/my-requests";
            case "ROLE_FACULTY" -> "/faculty/dashboard";
            default -> "/login?error=role";
        };

        response.sendRedirect(redirectUrl);
    }
}