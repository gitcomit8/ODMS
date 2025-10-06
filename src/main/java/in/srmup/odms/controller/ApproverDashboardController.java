package in.srmup.odms.controller;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.ApprovalHistoryRepository;
import in.srmup.odms.repository.EventRequestRepository;
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
@RequestMapping("/approver")
public class ApproverDashboardController {

    @Autowired
    private EventRequestRepository eventRequestRepository;

    @Autowired
    private EventRequestService eventRequestService;

    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Handle case where userDetails might be null (for dev purposes)
            String userRole = "ROLE_EVENT_COORDINATOR"; // Default for testing
            if (userDetails != null && userDetails.getAuthorities() != null) {
                userRole = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .findFirst().orElse("ROLE_EVENT_COORDINATOR");
            }

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

            // Ensure all lists are never null
            model.addAttribute("pendingMyAction", pendingMyAction != null ? pendingMyAction : Collections.emptyList());
            model.addAttribute("inProgress", inProgress != null ? inProgress : Collections.emptyList());
            model.addAttribute("finalized", finalized != null ? finalized : Collections.emptyList());
            model.addAttribute("userRole", userRole);

            return "approver-dashboard";
        } catch (Exception e) {
            System.err.println("Error in ApproverDashboardController.showDashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading approver dashboard: " + e.getMessage());
            model.addAttribute("pendingMyAction", Collections.emptyList());
            model.addAttribute("inProgress", Collections.emptyList());
            model.addAttribute("finalized", Collections.emptyList());
            model.addAttribute("userRole", "ROLE_EVENT_COORDINATOR");
            return "approver-dashboard";
        }
    }

    @GetMapping("/event-details/{id}")
    public String showEventDetails(@PathVariable("id") Long id, Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        EventRequest eventRequest = eventRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event Request not found"));

        var approvalHistory = approvalHistoryRepository.findByEventRequestIdOrderByActionTimestampAsc(id);

        model.addAttribute("event", eventRequest);
        model.addAttribute("participants", eventRequest.getParticipants());
        model.addAttribute("approvalHistory", approvalHistory);

        // Determine if current user can approve/reject
        if (userDetails != null) {
            String userRole = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst().orElse("");
            model.addAttribute("userRole", userRole);
            model.addAttribute("canApprove", canUserApprove(eventRequest.getStatus(), userRole));
        }

        return "event-details";
    }

    @PostMapping("/approve/{id}")
    public String approveRequest(@PathVariable("id") Long id,
                                 @RequestParam(required = false) String comments,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Check if user is authenticated
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You must be logged in to approve requests. Please log in first.");
                return "redirect:/dev-login";
            }

            eventRequestService.approveRequest(id, userDetails);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Request approved successfully! It has been forwarded to the next approver.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot approve: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error approving request: " + e.getMessage());
        }
        return "redirect:/approver/dashboard";
    }

    @PostMapping("/reject/{id}")
    public String rejectRequest(@PathVariable("id") Long id,
                                @RequestParam String reason,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            // Check if user is authenticated
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You must be logged in to reject requests. Please log in first.");
                return "redirect:/dev-login";
            }

            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Rejection reason is required");
            }
            eventRequestService.rejectRequest(id, userDetails, reason);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Request rejected successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot reject: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error rejecting request: " + e.getMessage());
        }
        return "redirect:/approver/dashboard";
    }

    private boolean canUserApprove(RequestStatus currentStatus, String userRole) {
        return switch (userRole) {
            case "ROLE_EVENT_COORDINATOR" -> currentStatus == RequestStatus.SUBMITTED;
            case "ROLE_STUDENT_WELFARE" -> currentStatus == RequestStatus.PENDING_WELFARE_APPROVAL;
            case "ROLE_HOD" -> currentStatus == RequestStatus.PENDING_HOD_APPROVAL;
            default -> false;
        };
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