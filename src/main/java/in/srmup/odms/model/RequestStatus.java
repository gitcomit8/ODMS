package in.srmup.odms.model;

public enum RequestStatus {
    SUBMITTED,                      // Initial status - Pending Faculty Coordinator approval
    PENDING_WELFARE_APPROVAL,       // Faculty Coordinator approved - Pending Student Welfare approval
    PENDING_HOD_APPROVAL,           // Student Welfare approved - Pending HOD approval
    APPROVED,                       // Fully approved by all three levels
    REJECTED                        // Rejected at any stage
}
