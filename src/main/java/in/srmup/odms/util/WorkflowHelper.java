package in.srmup.odms.util;

import in.srmup.odms.model.RequestStatus;

/**
 * Helper class for approval workflow information and validation
 * <p>
 * Workflow Sequence:
 * 1. SUBMITTED → Faculty Coordinator approves → PENDING_WELFARE_APPROVAL
 * 2. PENDING_WELFARE_APPROVAL → Student Welfare approves → PENDING_HOD_APPROVAL
 * 3. PENDING_HOD_APPROVAL → HOD approves → APPROVED
 */
public class WorkflowHelper {

    /**
     * Get human-readable status description
     */
    public static String getStatusDescription(RequestStatus status) {
        return switch (status) {
            case SUBMITTED -> "Pending Faculty Coordinator Approval (Step 1 of 3)";
            case PENDING_WELFARE_APPROVAL -> "Pending Student Welfare Approval (Step 2 of 3)";
            case PENDING_HOD_APPROVAL -> "Pending HOD Approval (Step 3 of 3)";
            case APPROVED -> "Fully Approved";
            case REJECTED -> "Rejected";
        };
    }

    /**
     * Get the next approver role for a given status
     */
    public static String getNextApprover(RequestStatus status) {
        return switch (status) {
            case SUBMITTED -> "Faculty Coordinator";
            case PENDING_WELFARE_APPROVAL -> "Student Welfare";
            case PENDING_HOD_APPROVAL -> "Head of Department";
            default -> "None";
        };
    }

    /**
     * Get workflow stage number (1-3)
     */
    public static int getWorkflowStage(RequestStatus status) {
        return switch (status) {
            case SUBMITTED -> 1;
            case PENDING_WELFARE_APPROVAL -> 2;
            case PENDING_HOD_APPROVAL -> 3;
            case APPROVED -> 4;
            case REJECTED -> 0;
        };
    }

    /**
     * Check if a specific role can act on a request with given status
     */
    public static boolean canRoleActOnStatus(String role, RequestStatus status) {
        return switch (role) {
            case "ROLE_EVENT_COORDINATOR" -> status == RequestStatus.SUBMITTED;
            case "ROLE_STUDENT_WELFARE" -> status == RequestStatus.PENDING_WELFARE_APPROVAL;
            case "ROLE_HOD" -> status == RequestStatus.PENDING_HOD_APPROVAL;
            case "ROLE_ADMIN" -> true; // Admin can act at any stage
            default -> false;
        };
    }

    /**
     * Get role display name
     */
    public static String getRoleDisplayName(String role) {
        return switch (role) {
            case "ROLE_EVENT_COORDINATOR" -> "Faculty Coordinator";
            case "ROLE_STUDENT_WELFARE" -> "Student Welfare";
            case "ROLE_HOD" -> "Head of Department";
            case "ROLE_ADMIN" -> "Administrator";
            case "ROLE_FACULTY" -> "Faculty";
            case "ROLE_STUDENT_ORGANIZER" -> "Student Organizer";
            default -> "Unknown";
        };
    }
}
