package in.srmup.odms.repository;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findAllByOrderByIdDesc();

    List<EventRequest> findByStatus(RequestStatus status);

    List<EventRequest> findByStatusAndApprovedDate(RequestStatus status, LocalDate approvedDate);

    List<EventRequest> findByStatusOrderByApprovedDateDesc(RequestStatus status);

    List<EventRequest> findByStatusInOrderByIdAsc(List<RequestStatus> statuses);

    List<EventRequest> findByStatusInOrderByIdDesc(List<RequestStatus> statuses);
}