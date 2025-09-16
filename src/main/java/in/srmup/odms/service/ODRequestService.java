package in.srmup.odms.service;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ODRequestService {
    private final EventRequestRepository eventRequestRepository;

    @Autowired
    public ODRequestService(EventRequestRepository eventRequestRepository) {
        this.eventRequestRepository = eventRequestRepository;
    }

    /**
     * Creates a new OD request.
     *
     * @param odRequest
     * @return
     */
    public EventRequest createOdRequest(EventRequest odRequest) {
        //Set an initial status for all new requests
        odRequest.setStatus(RequestStatus.SUBMITTED);

        //Save request to db using repo
        return eventRequestRepository.save(odRequest);
    }
}