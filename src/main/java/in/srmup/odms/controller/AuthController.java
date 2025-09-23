package in.srmup.odms.controller;

import in.srmup.odms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

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
    public String loginWithOtp(@RequestParam String email, @RequestParam String otp, RedirectAttributes redirectAttributes) {
        boolean isValid = authService.validateOtpAndLogin(email, otp);
        if (isValid) {
            return "redirect:/"; // Redirect to homepage on successful login
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired OTP.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/login";
        }
    }
}