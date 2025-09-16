package in.srmup.odms.repository;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findAllByOrderByIdDesc();

    List<EventRequest> findByStatus(RequestStatus status);
}