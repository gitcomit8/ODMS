package in.srmup.odms.repository;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findAllByIsHiddenFalseOrderByIdDesc();

    List<EventRequest> findByStatusAndIsHiddenFalse(RequestStatus status);

    List<EventRequest> findByStatusAndApprovedDateAndIsHiddenFalse(RequestStatus status, LocalDate approvedDate);

    List<EventRequest> findByIsHiddenFalseAndStatusOrderByApprovedDateDesc(RequestStatus status);

    List<EventRequest> findByIsHiddenFalseAndStatusInOrderByIdAsc(List<RequestStatus> statuses);

    List<EventRequest> findByIsHiddenFalseAndStatusInOrderByIdDesc(List<RequestStatus> statuses);
}