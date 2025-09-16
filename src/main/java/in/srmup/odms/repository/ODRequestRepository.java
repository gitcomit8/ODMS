package in.srmup.odms.repository;

import in.srmup.odms.model.ODRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ODRequestRepository extends JpaRepository<ODRequest, Long> {
    //TODO: add custom query methods
}