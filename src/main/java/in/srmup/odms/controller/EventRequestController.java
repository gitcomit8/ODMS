package in.srmup.odms.controller;


import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.FacultyMaster;
import in.srmup.odms.model.Participant;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import in.srmup.odms.repository.FacultyMasterRepository;
import in.srmup.odms.service.EventRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/event-requests")
public class EventRequestController {

    @Autowired
    private EventRequestService eventRequestService;

    @Autowired
    private EventRequestRepository eventRequestRepository;

    @Autowired
    private FacultyMasterRepository facultyMasterRepository;

    @GetMapping("/new")
    public String showRequestForm(Model model) {
        try {
            EventRequest eventRequest = new EventRequest();
            eventRequest.addParticipant(new Participant());
            model.addAttribute("eventRequest", eventRequest);

            // Load all faculty for selection
            List<FacultyMaster> allFaculty = facultyMasterRepository.findAll();
            model.addAttribute("allFaculty", allFaculty);

            return "event-request-form";
        } catch (Exception e) {
            System.err.println("Error in EventRequestController.showRequestForm: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading event request form: " + e.getMessage());
            return "error-page";
        }
    }

    @PostMapping("/submit")
    public String submitRequestForm(@ModelAttribute EventRequest eventRequest) {
        eventRequestService.createEventRequest(eventRequest);
        return "redirect:/event-requests/my-requests"; // Redirect to the student's dashboard
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success-page";
    }

    @GetMapping("/my-requests")
    public String showMyRequests(Model model) {
        List<EventRequest> requests = eventRequestRepository.findAllByIsHiddenFalseOrderByIdDesc();
        model.addAttribute("requests", requests);
        return "my-requests";
    }

    @GetMapping("/pending")
    public String showPendingRequests(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String userRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        RequestStatus requiredStatus = null;
        switch (userRole) {
            case "ROLE_EVENT_COORDINATOR":
                requiredStatus = RequestStatus.SUBMITTED;
                break;
            case "ROLE_STUDENT_WELFARE":
                requiredStatus = RequestStatus.PENDING_WELFARE_APPROVAL;
                break;
            case "ROLE_HOD":
                requiredStatus = RequestStatus.PENDING_HOD_APPROVAL;
                break;
        }

        List<EventRequest> pendingRequests;
        if (requiredStatus != null) {
            pendingRequests = eventRequestRepository.findByStatusAndIsHiddenFalse(requiredStatus); // You'll need to create this method in your repo
        } else {
            pendingRequests = Collections.emptyList(); // Or handle as needed
        }

        model.addAttribute("requests", pendingRequests);
        return "pending-requests";
    }
}