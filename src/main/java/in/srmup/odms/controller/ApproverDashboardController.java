package in.srmup.odms.controller;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/approver")
public class ApproverDashboardController {

    @Autowired
    private EventRequestRepository eventRequestRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String userRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("");

        // 1. Get requests "Pending My Action"
        RequestStatus myPendingStatus = getRequiredStatusForRole(userRole);
        List<EventRequest> pendingMyAction = (myPendingStatus != null)
                ? eventRequestRepository.findByStatusAndIsHiddenFalse(myPendingStatus)
                : Collections.emptyList();

        // 2. Get requests "In Progress" (approved by me, waiting for others)
        List<RequestStatus> inProgressStatuses = getInProgressStatuses(userRole);
        List<EventRequest> inProgress = eventRequestRepository.findByIsHiddenFalseAndStatusInOrderByIdAsc(inProgressStatuses);

        // 3. Get "Finalized" requests (Approved or Rejected)
        List<RequestStatus> finalStatuses = List.of(RequestStatus.APPROVED, RequestStatus.REJECTED);
        List<EventRequest> finalized = eventRequestRepository.findByIsHiddenFalseAndStatusInOrderByIdDesc(finalStatuses);

        model.addAttribute("pendingMyAction", pendingMyAction);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("finalized", finalized);

        return "approver-dashboard";
    }

    // In controller/ApproverDashboardController.java

    @GetMapping("/event-details/{id}")
    public String showEventDetails(@PathVariable("id") Long id, Model model) {
        EventRequest eventRequest = eventRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event Request not found"));

        model.addAttribute("event", eventRequest);
        model.addAttribute("participants", eventRequest.getParticipants());

        return "event-details"; // The name of our new HTML template
    }

    private RequestStatus getRequiredStatusForRole(String role) {
        return switch (role) {
            case "ROLE_EVENT_COORDINATOR" -> RequestStatus.SUBMITTED;
            case "ROLE_STUDENT_WELFARE" -> RequestStatus.PENDING_WELFARE_APPROVAL;
            case "ROLE_HOD" -> RequestStatus.PENDING_HOD_APPROVAL;
            default -> null;
        };
    }

    private List<RequestStatus> getInProgressStatuses(String role) {
        return switch (role) {
            case "ROLE_EVENT_COORDINATOR" ->
                    List.of(RequestStatus.PENDING_WELFARE_APPROVAL, RequestStatus.PENDING_HOD_APPROVAL);
            case "ROLE_STUDENT_WELFARE" -> List.of(RequestStatus.PENDING_HOD_APPROVAL);
            default -> Collections.emptyList();
        };
    }
}