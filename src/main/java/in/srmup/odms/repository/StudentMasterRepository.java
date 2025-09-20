package in.srmup.odms.repository;

import in.srmup.odms.model.StudentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentMasterRepository extends JpaRepository<StudentMaster, String> {
}