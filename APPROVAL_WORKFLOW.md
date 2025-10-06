# Multilevel Approval Workflow Documentation

## Overview

The ODMS (On-Duty Management System) implements a strict three-level approval workflow for event requests. The workflow
ensures that each request is reviewed and approved by the appropriate authorities in the correct sequence.

## Workflow Sequence

The approval workflow follows this exact sequence:

```
1. SUBMITTED
   ↓ (Faculty Coordinator approves)
2. PENDING_WELFARE_APPROVAL
   ↓ (Student Welfare approves)
3. PENDING_HOD_APPROVAL
   ↓ (HOD approves)
4. APPROVED
```

### Detailed Workflow Steps

#### Step 1: Faculty Coordinator Approval

- **Status:** `SUBMITTED`
- **Approver Role:** `ROLE_EVENT_COORDINATOR` (Faculty Coordinator)
- **Action:** Reviews the event request details and participant information
- **On Approval:** Status changes to `PENDING_WELFARE_APPROVAL`
- **On Rejection:** Status changes to `REJECTED`

#### Step 2: Student Welfare Approval

- **Status:** `PENDING_WELFARE_APPROVAL`
- **Approver Role:** `ROLE_STUDENT_WELFARE` (Student Welfare Officer)
- **Action:** Reviews the request approved by Faculty Coordinator
- **On Approval:** Status changes to `PENDING_HOD_APPROVAL`
- **On Rejection:** Status changes to `REJECTED`

#### Step 3: HOD Approval

- **Status:** `PENDING_HOD_APPROVAL`
- **Approver Role:** `ROLE_HOD` (Head of Department)
- **Action:** Final review and approval
- **On Approval:** Status changes to `APPROVED` and OD leave counts are updated
- **On Rejection:** Status changes to `REJECTED`

## Workflow Enforcement

### Strict Role-Based Access Control

Each approver can **ONLY** act on requests that are at their designated approval stage:

| Role                | Can Approve Status         | Cannot Approve                                     |
|---------------------|----------------------------|----------------------------------------------------|
| Faculty Coordinator | `SUBMITTED`                | `PENDING_WELFARE_APPROVAL`, `PENDING_HOD_APPROVAL` |
| Student Welfare     | `PENDING_WELFARE_APPROVAL` | `SUBMITTED`, `PENDING_HOD_APPROVAL`                |
| HOD                 | `PENDING_HOD_APPROVAL`     | `SUBMITTED`, `PENDING_WELFARE_APPROVAL`            |
| Admin               | Any status                 | None (can act at any stage)                        |

### Validation Rules

1. **Sequential Approval Required:** A request cannot skip approval levels. It must be approved by:
    - Faculty Coordinator first
    - Then Student Welfare
    - Finally HOD

2. **Role Validation:** The system validates that the approver's role matches the current status before allowing
   approval.

3. **Status Validation:** Before approving, the system checks that the request is in the correct status for that role.

4. **Rejection at Any Stage:** Each approver can reject a request at their stage, but only at their designated stage.

## Implementation Details

### Service Layer (`EventRequestService`)

The `approveRequest()` method implements strict workflow validation:

```java
// Faculty Coordinator can only approve SUBMITTED requests
if ("ROLE_EVENT_COORDINATOR".equals(approverRole)) {
    if (request.getStatus() != RequestStatus.SUBMITTED) {
        throw new IllegalStateException("Faculty Coordinator can only approve requests in SUBMITTED status");
    }
    toStatus = RequestStatus.PENDING_WELFARE_APPROVAL;
}
```

### Error Handling

If a user tries to approve a request at the wrong stage, they will receive a clear error message:

- ✅ Correct: Faculty Coordinator approves `SUBMITTED` request
- ❌ Error: Faculty Coordinator tries to approve `PENDING_WELFARE_APPROVAL` request
    - Message: "Faculty Coordinator can only approve requests in SUBMITTED status. Current status:
      PENDING_WELFARE_APPROVAL"

### Approval History Tracking

Every approval and rejection action is recorded in the `approval_history` table with:

- Approver role
- Approver email
- Approver name
- Status transition (from → to)
- Action (APPROVED/REJECTED)
- Timestamp
- Comments/Reason

## Dashboard Views

### Pending My Action

Each approver sees only requests that are waiting for their approval:

- **Faculty Coordinator:** Sees requests in `SUBMITTED` status
- **Student Welfare:** Sees requests in `PENDING_WELFARE_APPROVAL` status
- **HOD:** Sees requests in `PENDING_HOD_APPROVAL` status

### In Progress

Each approver can see requests they've already approved that are waiting for subsequent approvals:

- **Faculty Coordinator:** Sees `PENDING_WELFARE_APPROVAL` and `PENDING_HOD_APPROVAL`
- **Student Welfare:** Sees `PENDING_HOD_APPROVAL`
- **HOD:** Empty (no further approvals after HOD)

### Finalized

All approvers can see requests that are:

- `APPROVED` - Fully approved by all three levels
- `REJECTED` - Rejected at any stage

## Request Status Descriptions

| Status                     | Display Name                                         | Workflow Stage |
|----------------------------|------------------------------------------------------|----------------|
| `SUBMITTED`                | "Pending Faculty Coordinator Approval (Step 1 of 3)" | 1              |
| `PENDING_WELFARE_APPROVAL` | "Pending Student Welfare Approval (Step 2 of 3)"     | 2              |
| `PENDING_HOD_APPROVAL`     | "Pending HOD Approval (Step 3 of 3)"                 | 3              |
| `APPROVED`                 | "Fully Approved"                                     | Complete       |
| `REJECTED`                 | "Rejected"                                           | Terminated     |

## OD Leave Count Updates

OD leave counts are incremented **ONLY** when the request reaches `APPROVED` status (after HOD approval). This ensures
that:

- Partial approvals don't affect leave counts
- Rejected requests don't impact leave counts
- Only fully approved requests update student records

## Code References

### Key Files

1. **`RequestStatus.java`**
    - Defines all possible request statuses with comments

2. **`EventRequestService.java`**
    - `approveRequest()`: Implements strict workflow validation
    - `rejectRequest()`: Validates rejection permissions
    - `canRejectAtCurrentStage()`: Checks if role can reject at current status

3. **`ApproverDashboardController.java`**
    - `getRequiredStatusForRole()`: Maps roles to their pending statuses
    - `canUserApprove()`: Validates if user can approve current status
    - `getInProgressStatuses()`: Returns statuses for "In Progress" section

4. **`WorkflowHelper.java`**
    - Utility methods for workflow information
    - Status descriptions and role display names

### Database Tables

- **`event_requests`**: Stores request data and current status
- **`approval_history`**: Tracks all approval/rejection actions with full audit trail

## Testing the Workflow

### Test Scenario 1: Successful Approval Flow

1. Student submits request → Status: `SUBMITTED`
2. Faculty Coordinator approves → Status: `PENDING_WELFARE_APPROVAL`
3. Student Welfare approves → Status: `PENDING_HOD_APPROVAL`
4. HOD approves → Status: `APPROVED`

### Test Scenario 2: Rejection at Step 2

1. Student submits request → Status: `SUBMITTED`
2. Faculty Coordinator approves → Status: `PENDING_WELFARE_APPROVAL`
3. Student Welfare rejects → Status: `REJECTED`

### Test Scenario 3: Invalid Approval Attempt

1. Student submits request → Status: `SUBMITTED`
2. Student Welfare tries to approve → **ERROR**: Cannot approve, request not at their stage
3. Faculty Coordinator approves → Status: `PENDING_WELFARE_APPROVAL`
4. Student Welfare approves → Status: `PENDING_HOD_APPROVAL`

## Troubleshooting

### "Cannot approve: User does not have permission..."

**Cause:** The approver's role doesn't match the current status of the request.

**Solution:**

- Check the current status of the request
- Verify that the request is at your approval stage
- Wait for previous approvers to approve before attempting

### "Cannot reject: User with role X cannot reject requests in status Y"

**Cause:** Trying to reject a request that's not at your approval stage.

**Solution:**

- You can only reject requests that are waiting for your approval
- Check the request status in the dashboard

## Best Practices

1. **Always check the current status** before attempting to approve/reject
2. **Review approval history** to see who has already approved
3. **Provide clear rejection reasons** to help requesters improve future submissions
4. **Use the dashboard filters** to see only requests relevant to your role
5. **Act promptly** on pending requests to keep the workflow moving

## Admin Override

Users with `ROLE_ADMIN` can:

- Approve/reject requests at any stage
- Override workflow restrictions (use with caution)
- View all requests regardless of status

This should only be used for exceptional circumstances or system administration.

## Future Enhancements

Potential improvements to consider:

1. Email notifications at each approval stage
2. Deadline tracking for approvals
3. Automatic escalation for delayed approvals
4. Workflow statistics and reports
5. Bulk approval capabilities
6. Comments/feedback during approval

## Support

For issues or questions about the approval workflow:

- Check the approval history for audit trail
- Review error messages for specific validation failures
- Contact system administrator for role/permission issues
