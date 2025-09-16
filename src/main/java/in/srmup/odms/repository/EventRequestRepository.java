package in.srmup.odms.repository;

import in.srmup.odms.model.EventRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    //TODO: add custom query methods
}