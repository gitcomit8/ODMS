package in.srmup.odms.controller;

import in.srmup.odms.config.security.CustomAuthSuccessHandler;
import in.srmup.odms.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomAuthSuccessHandler customAuthSuccessHandler;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/generate-otp")
    public String generateOtp(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            authService.generateAndSendOtp(email);
            redirectAttributes.addFlashAttribute("successMessage", "An OTP has been sent to your email.");
            redirectAttributes.addFlashAttribute("email", email); // Pass email to the next view
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/login";
    }

    @PostMapping("/login-with-otp")
    public void loginWithOtp(@RequestParam String email, @RequestParam String otp,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("=== DEBUG: Starting OTP validation for email: " + email);

        boolean isValid = authService.validateOtpAndLogin(email, otp);

        if (isValid) {
            System.out.println("=== DEBUG: OTP valid, getting authentication from SecurityContext");

            // Get the authentication that was set by AuthService
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                System.out.println("=== DEBUG: Authentication found, calling CustomAuthSuccessHandler");

                // Manually invoke your CustomAuthSuccessHandler
                customAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                // Important: return void since response is already handled
            } else {
                System.out.println("=== DEBUG: No authentication found, redirecting to login with error");
                response.sendRedirect("/login?error=auth");
            }
        } else {
            System.out.println("=== DEBUG: OTP validation failed");
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired OTP.");
            redirectAttributes.addFlashAttribute("email", email);
            response.sendRedirect("/login");
        }
    }
}