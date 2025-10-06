package in.srmup.odms.repository;

import in.srmup.odms.model.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByEventRequestIdOrderByActionTimestampAsc(Long eventRequestId);
}
