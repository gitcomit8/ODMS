package in.srmup.odms.controller;


import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.Participant;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import in.srmup.odms.service.EventRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/event-requests")
public class EventRequestController {

    @Autowired
    private EventRequestService eventRequestService;

    @Autowired
    private EventRequestRepository eventRequestRepository;

    @GetMapping("/new")
    public String showRequestForm(Model model) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.addParticipant(new Participant());
        model.addAttribute("eventRequest", eventRequest);
        return "event-request-form";
    }

    @PostMapping("/submit")
    public String submitRequestForm(@ModelAttribute EventRequest eventRequest) {
        eventRequest.setStatus(RequestStatus.SUBMITTED);
        for (Participant p : eventRequest.getParticipants()) {
            p.setEventRequest(eventRequest);
        }
        eventRequestRepository.save(eventRequest);
        return "redirect:/event-requests/success";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success-page";
    }

    @GetMapping("/my-requests")
    public String showMyRequests(Model model) {
        List<EventRequest> requests = eventRequestRepository.findAllByOrderByIdDesc();
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
            pendingRequests = eventRequestRepository.findByStatus(requiredStatus); // You'll need to create this method in your repo
        } else {
            pendingRequests = Collections.emptyList(); // Or handle as needed
        }

        model.addAttribute("requests", pendingRequests);
        return "pending-requests";
    }

    @PostMapping("/approve/{id}")
    public String approveRequest(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        eventRequestService.approveRequest(id, userDetails);
        return "redirect:/approver/dashboard";
    }

    @PostMapping("/reject/{id}")
    public String rejectRequest(@PathVariable("id") Long id) {
        eventRequestService.rejectRequest(id); // Call the service
        return "redirect:/approver/dashboard";
    }
}