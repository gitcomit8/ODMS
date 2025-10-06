package in.srmup.odms.repository;

import in.srmup.odms.model.FacultyMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyMasterRepository extends JpaRepository<FacultyMaster, Long> {

    // Spring Data JPA will automatically create the query for this method name
    Optional<FacultyMaster> findByBranchAndSection(String branch, String section);

    Optional<FacultyMaster> findByFacultyEmail(String email);
}