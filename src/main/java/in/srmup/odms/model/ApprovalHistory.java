package in.srmup.odms.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_request_id", nullable = false)
    private EventRequest eventRequest;

    @Column(nullable = false)
    private String approverRole;

    @Column(nullable = false)
    private String approverEmail;

    @Column(nullable = false)
    private String approverName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus toStatus;

    @Column(nullable = false)
    private LocalDateTime actionTimestamp;

    @Column(nullable = false)
    private String action; // "APPROVED" or "REJECTED"

    @Column(length = 1000)
    private String comments;

    // Constructors
    public ApprovalHistory() {
    }

    public ApprovalHistory(EventRequest eventRequest, String approverRole, String approverEmail,
                           String approverName, RequestStatus fromStatus, RequestStatus toStatus,
                           String action, String comments) {
        this.eventRequest = eventRequest;
        this.approverRole = approverRole;
        this.approverEmail = approverEmail;
        this.approverName = approverName;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actionTimestamp = LocalDateTime.now();
        this.action = action;
        this.comments = comments;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventRequest getEventRequest() {
        return eventRequest;
    }

    public void setEventRequest(EventRequest eventRequest) {
        this.eventRequest = eventRequest;
    }

    public String getApproverRole() {
        return approverRole;
    }

    public void setApproverRole(String approverRole) {
        this.approverRole = approverRole;
    }

    public String getApproverEmail() {
        return approverEmail;
    }

    public void setApproverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public RequestStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(RequestStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public RequestStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(RequestStatus toStatus) {
        this.toStatus = toStatus;
    }

    public LocalDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    public void setActionTimestamp(LocalDateTime actionTimestamp) {
        this.actionTimestamp = actionTimestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
