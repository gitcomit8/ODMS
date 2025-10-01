package in.srmup.odms.controller;

import in.srmup.odms.model.User;
import in.srmup.odms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        try {
            List<User> allUsers = adminService.findAllUsers();
            model.addAttribute("users", allUsers);
            // This list will populate the dropdowns in the form
            model.addAttribute("allRoles", List.of("ROLE_ADMIN", "ROLE_STUDENT_ORGANIZER", "ROLE_EVENT_COORDINATOR", "ROLE_STUDENT_WELFARE", "ROLE_HOD", "ROLE_FACULTY"));
            return "admin-dashboard"; // The name of our new HTML file
        } catch (Exception e) {
            System.err.println("Error in AdminController.showAdminDashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading admin dashboard: " + e.getMessage());
            return "error-page";
        }
    }

    @PostMapping("/update-role")
    public String updateUserRole(@RequestParam("userId") Long userId, @RequestParam("role") String role) {
        adminService.updateUserRole(userId, role);
        return "redirect:/admin/dashboard";
    }
}