package in.srmup.odms.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@Profile("dev")
@ConditionalOnProperty(name = "app.security.dev-login.enabled", havingValue = "true")
public class DevLoginController {

    @GetMapping("/dev-login")
    public String showDevLoginPage(Model model) {
        // List of all roles to populate the dropdown
        model.addAttribute("allRoles", List.of("ROLE_ADMIN", "ROLE_STUDENT_ORGANIZER", "ROLE_EVENT_COORDINATOR", "ROLE_STUDENT_WELFARE", "ROLE_HOD", "ROLE_FACULTY"));
        return "dev-login";
    }

    @PostMapping("/dev-login")
    public String processDevLogin(@RequestParam String username, @RequestParam String role) {
        // Create a fake authentication object with the chosen role
        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );

        // Manually set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Redirect to the role-based dashboard (using the same logic as your success handler)
        return switch (role) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_EVENT_COORDINATOR", "ROLE_STUDENT_WELFARE", "ROLE_HOD" -> "redirect:/approver/dashboard";
            case "ROLE_STUDENT_ORGANIZER" -> "redirect:/event-requests/my-requests";
            case "ROLE_FACULTY" -> "redirect:/faculty/dashboard";
            default -> "redirect:/";
        };
    }
}