package in.srmup.odms.service;

import in.srmup.odms.model.ODRequest;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.ODRequestRepository;

@Service
public class ODRequestService {
    private final ODRequestRepository odRequestRepository;

    @Autowired
    public ODRequestService(ODRequestRepository odRequestRepository) {
        this.odRequestRepository = odRequestRepository;
    }

    /**
     * Creates a new OD request.
     *
     * @param odRequest The request object to be saved.
     * @return The saved request with its generated ID and initial status.
     */
    public ODRequest createOdRequest(ODRequest odRequest) {
        //Set initial status for all new requests
        odRequest.setStatus(RequestStatus.SUBMITTED);

        //Save request to db using repo
        return odRequestRepository.save(odRequest);
    }
}