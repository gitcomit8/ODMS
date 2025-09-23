package in.srmup.odms.controller;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private EventRequestRepository eventRequestRepository;

    @GetMapping("/dashboard")
    public String showFacultyDashboard(Model model) {
        // Use our new repository method to get all fully approved requests
        List<EventRequest> approvedRequests = eventRequestRepository.findByIsHiddenFalseAndStatusOrderByApprovedDateDesc(RequestStatus.APPROVED);
        model.addAttribute("requests", approvedRequests);
        return "faculty-dashboard"; // The name of our new HTML file
    }
}