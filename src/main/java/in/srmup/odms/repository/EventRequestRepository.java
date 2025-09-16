package in.srmup.odms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRequestRepository extends JpaRepository<ODRequest, Long> {
    //TODO: add custom query methods
}