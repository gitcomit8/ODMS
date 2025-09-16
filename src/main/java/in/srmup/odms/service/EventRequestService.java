package in.srmup.odms.service;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventRequestService {

    @Autowired
    private EventRequestRepository eventRequestRepository;

    public void approveRequest(Long id) {
        Optional<EventRequest> requestOpt = eventRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            EventRequest request = requestOpt.get();

            // The State Machine Logic
            if (request.getStatus() == RequestStatus.SUBMITTED) {
                request.setStatus(RequestStatus.PENDING_WELFARE_APPROVAL);
            } else if (request.getStatus() == RequestStatus.PENDING_WELFARE_APPROVAL) {
                request.setStatus(RequestStatus.PENDING_HOD_APPROVAL);
            } else if (request.getStatus() == RequestStatus.PENDING_HOD_APPROVAL) {
                request.setStatus(RequestStatus.APPROVED);
            }

            eventRequestRepository.save(request);
        }
    }

    public void rejectRequest(Long id) {
        eventRequestRepository.findById(id).ifPresent(request -> {
            request.setStatus(RequestStatus.REJECTED);
            eventRequestRepository.save(request);
        });
    }
}