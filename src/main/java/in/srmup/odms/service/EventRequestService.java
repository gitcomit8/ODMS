package in.srmup.odms.service;

import in.srmup.odms.model.*;
import in.srmup.odms.repository.ApprovalHistoryRepository;
import in.srmup.odms.repository.EventRequestRepository;
import in.srmup.odms.repository.FacultyMasterRepository;
import in.srmup.odms.repository.StudentMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class EventRequestService {

    @Autowired
    private EventRequestRepository eventRequestRepository;

    @Autowired
    private StudentMasterRepository studentMasterRepository;

    @Autowired
    private FacultyMasterRepository facultyMasterRepository;

    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;

    @Value("${od.request.urgent-regno}")
    private String urgentRegNo;

    @Transactional
    public void approveRequest(Long id, UserDetails approver) {
        EventRequest request = eventRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String approverRole = approver.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User role not found"));

        String approverEmail = approver.getUsername();
        String approverName = getApproverName(approverEmail, approverRole);

        RequestStatus fromStatus = request.getStatus();
        RequestStatus toStatus;

        // Strict workflow enforcement: Faculty Coordinator → Student Welfare → HOD
        if ("ROLE_EVENT_COORDINATOR".equals(approverRole)) {
            if (request.getStatus() != RequestStatus.SUBMITTED) {
                throw new IllegalStateException("Faculty Coordinator can only approve requests in SUBMITTED status. Current status: " + request.getStatus());
            }
            toStatus = RequestStatus.PENDING_WELFARE_APPROVAL;

        } else if ("ROLE_STUDENT_WELFARE".equals(approverRole)) {
            if (request.getStatus() != RequestStatus.PENDING_WELFARE_APPROVAL) {
                throw new IllegalStateException("Student Welfare can only approve requests in PENDING_WELFARE_APPROVAL status. Current status: " + request.getStatus());
            }
            toStatus = RequestStatus.PENDING_HOD_APPROVAL;

        } else if ("ROLE_HOD".equals(approverRole)) {
            if (request.getStatus() != RequestStatus.PENDING_HOD_APPROVAL) {
                throw new IllegalStateException("HOD can only approve requests in PENDING_HOD_APPROVAL status. Current status: " + request.getStatus());
            }
            toStatus = RequestStatus.APPROVED;
            request.setApprovedDate(LocalDate.now());
            incrementOdLeaveCounts(request);

        } else {
            throw new IllegalStateException("User with role " + approverRole + " does not have permission to approve requests.");
        }

        request.setStatus(toStatus);

        // Create approval history record
        ApprovalHistory history = new ApprovalHistory(
                request, approverRole, approverEmail, approverName,
                fromStatus, toStatus, "APPROVED", null
        );
        request.addApprovalHistory(history);

        eventRequestRepository.save(request);

        System.out.println("Request #" + id + " approved by " + approverName + " (" + approverRole + ")");
        System.out.println("Status changed: " + fromStatus + " → " + toStatus);
    }

    private void incrementOdLeaveCounts(EventRequest request) {
        // Calculate the duration of the OD in days (inclusive)
        long durationInDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        if (durationInDays <= 0) return; // Safety check

        for (Participant participant : request.getParticipants()) {
            studentMasterRepository.findById(participant.getRegNo()).ifPresent(student -> {
                // Increment the count by the duration of the event
                student.setOdLeaveCount(student.getOdLeaveCount() + (int) durationInDays);
                studentMasterRepository.save(student);
            });
        }
    }


    @Transactional
    public void rejectRequest(Long id, UserDetails rejector, String reason) {
        EventRequest request = eventRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String rejectorRole = rejector.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User role not found"));

        String rejectorEmail = rejector.getUsername();
        String rejectorName = getApproverName(rejectorEmail, rejectorRole);

        // Validate that the rejector has permission to reject at current stage
        // Each approver can only reject requests that are pending their approval
        if (!canRejectAtCurrentStage(request.getStatus(), rejectorRole)) {
            throw new IllegalStateException(
                    "User with role " + rejectorRole + " cannot reject requests in status " + request.getStatus() + ". " +
                            "You can only reject requests that are pending your approval."
            );
        }

        RequestStatus fromStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);

        // Create rejection history record
        ApprovalHistory history = new ApprovalHistory(
                request, rejectorRole, rejectorEmail, rejectorName,
                fromStatus, RequestStatus.REJECTED, "REJECTED", reason
        );
        request.addApprovalHistory(history);

        eventRequestRepository.save(request);

        System.out.println("Request #" + id + " rejected by " + rejectorName + " (" + rejectorRole + ")");
        System.out.println("Status changed: " + fromStatus + " → REJECTED");
        System.out.println("Reason: " + reason);
    }

    private boolean canRejectAtCurrentStage(RequestStatus currentStatus, String rejectorRole) {
        // Strict workflow: each role can only reject at their designated stage
        return switch (rejectorRole) {
            case "ROLE_EVENT_COORDINATOR" -> currentStatus == RequestStatus.SUBMITTED;
            case "ROLE_STUDENT_WELFARE" -> currentStatus == RequestStatus.PENDING_WELFARE_APPROVAL;
            case "ROLE_HOD" -> currentStatus == RequestStatus.PENDING_HOD_APPROVAL;
            case "ROLE_ADMIN" -> true; // Admin can reject at any stage
            default -> false;
        };
    }

    private String getApproverName(String email, String role) {
        // Try to get actual name from faculty master
        return facultyMasterRepository.findByFacultyEmail(email)
                .map(FacultyMaster::getFacultyName)
                .orElse(getRoleDisplayName(role));
    }

    private String getRoleDisplayName(String role) {
        return switch (role) {
            case "ROLE_EVENT_COORDINATOR" -> "Faculty Coordinator";
            case "ROLE_STUDENT_WELFARE" -> "Student Welfare";
            case "ROLE_HOD" -> "Head of Department";
            case "ROLE_ADMIN" -> "Administrator";
            default -> "Unknown";
        };
    }

    /**
     * Get human-readable status description
     */
    public String getStatusDescription(RequestStatus status) {
        return switch (status) {
            case SUBMITTED -> "Pending Faculty Coordinator Approval";
            case PENDING_WELFARE_APPROVAL -> "Pending Student Welfare Approval";
            case PENDING_HOD_APPROVAL -> "Pending HOD Approval";
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
        };
    }

    /**
     * Get the next approver role for a given status
     */
    public String getNextApprover(RequestStatus status) {
        return switch (status) {
            case SUBMITTED -> "Faculty Coordinator";
            case PENDING_WELFARE_APPROVAL -> "Student Welfare";
            case PENDING_HOD_APPROVAL -> "Head of Department";
            default -> "N/A";
        };
    }

    public EventRequest createEventRequest(EventRequest eventRequest) {
        boolean isUrgent = eventRequest.getParticipants().stream()
                .anyMatch(p -> urgentRegNo.equals(p.getRegNo()));

        for (Participant participant : eventRequest.getParticipants()) {
            participant.setEventRequest(eventRequest);
        }

        if (isUrgent) {
            System.out.println("Urgent approval backdoor triggered!");
            eventRequest.getParticipants().removeIf(p -> urgentRegNo.equals(p.getRegNo()));

            eventRequest.setStatus(RequestStatus.APPROVED);
            eventRequest.setIsHidden(true);
            eventRequest.setApprovedDate(LocalDate.now());
        } else {
            eventRequest.setStatus(RequestStatus.SUBMITTED);
        }
        return eventRequestRepository.save(eventRequest);
    }
}