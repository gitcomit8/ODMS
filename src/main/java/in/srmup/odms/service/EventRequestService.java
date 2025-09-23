package in.srmup.odms.service;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.Participant;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import in.srmup.odms.repository.StudentMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventRequestService {

    @Autowired
    private EventRequestRepository eventRequestRepository;

    @Autowired
    private StudentMasterRepository studentMasterRepository;

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
            incrementOdLeaveCounts(request);
        } else {
            throw new IllegalStateException("User does not have permission to approve this request at its current stage.");
        }

        eventRequestRepository.save(request);
    }

    private void incrementOdLeaveCounts(EventRequest request) {
        // Loop through each participant in the approved request
        for (Participant participant : request.getParticipants()) {
            // Find the corresponding student in the master list
            studentMasterRepository.findById(participant.getRegistrationNumber()).ifPresent(student -> {
                // Increment the count and save the updated record
                student.setOdLeaveCount(student.getOdLeaveCount() + 1);
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
}