# Multilevel Approval Workflow - Fix Summary

## Problem Statement

The multilevel approval workflow was not properly enforcing the correct sequence of approvals. The flow should strictly
follow:
**Faculty Coordinator → Student Welfare → HOD**

## Changes Made

### 1. Enhanced RequestStatus Enum

**File:** `src/main/java/in/srmup/odms/model/RequestStatus.java`

- Added comprehensive comments to clarify each status
- Documented the workflow sequence
- Clarified that SUBMITTED means "Pending Faculty Coordinator Approval"

### 2. Strengthened Approval Validation

**File:** `src/main/java/in/srmup/odms/service/EventRequestService.java`

#### `approveRequest()` Method

- ✅ Added strict role validation for each approval stage
- ✅ Throws `IllegalStateException` if role doesn't match current status
- ✅ Clear error messages indicating what went wrong
- ✅ Logs approval actions with role and status changes
- ✅ Ensures Faculty Coordinator can only approve SUBMITTED requests
- ✅ Ensures Student Welfare can only approve PENDING_WELFARE_APPROVAL requests
- ✅ Ensures HOD can only approve PENDING_HOD_APPROVAL requests

Before:

```java
if ("ROLE_EVENT_COORDINATOR".equals(approverRole) && request.getStatus() == RequestStatus.SUBMITTED) {
    toStatus = RequestStatus.PENDING_WELFARE_APPROVAL;
} else if (...) {
    // Similar loose validation
}
```

After:

```java
if ("ROLE_EVENT_COORDINATOR".equals(approverRole)) {
    if (request.getStatus() != RequestStatus.SUBMITTED) {
        throw new IllegalStateException("Faculty Coordinator can only approve requests in SUBMITTED status. Current status: " + request.getStatus());
    }
    toStatus = RequestStatus.PENDING_WELFARE_APPROVAL;
}
```

#### `rejectRequest()` Method

- ✅ Enhanced validation to ensure rejector can only reject at their stage
- ✅ Better error messages for rejection validation failures
- ✅ Logs rejection actions with reason

#### Helper Methods Added

- `getStatusDescription()`: Returns human-readable status descriptions
- `getNextApprover()`: Returns who needs to approve next
- Updated `getRoleDisplayName()`: Changed "Event Coordinator" to "Faculty Coordinator"

### 3. Improved Error Handling in Controllers

**Files:**

- `src/main/java/in/srmup/odms/controller/ApproverDashboardController.java`
- `src/main/java/in/srmup/odms/controller/EventRequestController.java`

#### Changes:

- ✅ Added specific exception handling for `IllegalStateException`
- ✅ Added specific exception handling for `IllegalArgumentException`
- ✅ Better user feedback with informative error messages
- ✅ Success messages clarify next steps ("forwarded to next approver")
- ✅ Added `RedirectAttributes` import to EventRequestController

### 4. Created WorkflowHelper Utility Class

**File:** `src/main/java/in/srmup/odms/util/WorkflowHelper.java`

A new utility class providing:

- ✅ `getStatusDescription()`: Human-readable status with step numbers
- ✅ `getNextApprover()`: Shows who approves next
- ✅ `getWorkflowStage()`: Returns stage number (1-3)
- ✅ `canRoleActOnStatus()`: Validates if role can act on status
- ✅ `getRoleDisplayName()`: Consistent role display names

### 5. Documentation

**Files Created:**

- ✅ `APPROVAL_WORKFLOW.md`: Comprehensive workflow documentation
- ✅ `WORKFLOW_FIX_SUMMARY.md`: This file

## Workflow Enforcement Details

### Strict Sequential Approval

| Current Status           | Who Can Approve     | Next Status              | Who CANNOT Approve                   |
|--------------------------|---------------------|--------------------------|--------------------------------------|
| SUBMITTED                | Faculty Coordinator | PENDING_WELFARE_APPROVAL | Student Welfare, HOD                 |
| PENDING_WELFARE_APPROVAL | Student Welfare     | PENDING_HOD_APPROVAL     | Faculty Coordinator, HOD             |
| PENDING_HOD_APPROVAL     | HOD                 | APPROVED                 | Faculty Coordinator, Student Welfare |

### Validation Examples

#### ✅ Valid Approval Sequence

1. Request submitted → Status: `SUBMITTED`
2. Faculty Coordinator approves → Status: `PENDING_WELFARE_APPROVAL`
3. Student Welfare approves → Status: `PENDING_HOD_APPROVAL`
4. HOD approves → Status: `APPROVED` + OD counts updated

#### ❌ Invalid Approval Attempts

1. Request submitted → Status: `SUBMITTED`
2. Student Welfare tries to approve → **ERROR**: "Student Welfare can only approve requests in PENDING_WELFARE_APPROVAL
   status"
3. Faculty Coordinator approves → Status: `PENDING_WELFARE_APPROVAL`
4. HOD tries to approve → **ERROR**: "HOD can only approve requests in PENDING_HOD_APPROVAL status"

### Rejection Rules

Each approver can **ONLY** reject requests at their stage:

- Faculty Coordinator: Can reject `SUBMITTED` requests
- Student Welfare: Can reject `PENDING_WELFARE_APPROVAL` requests
- HOD: Can reject `PENDING_HOD_APPROVAL` requests
- Admin: Can reject at any stage

## Testing Checklist

### Test Case 1: Normal Flow ✓

- [ ] Student submits request
- [ ] Faculty Coordinator sees it in "Pending My Action"
- [ ] Faculty Coordinator approves
- [ ] Student Welfare sees it in "Pending My Action"
- [ ] Student Welfare approves
- [ ] HOD sees it in "Pending My Action"
- [ ] HOD approves
- [ ] Status becomes APPROVED
- [ ] OD counts are updated

### Test Case 2: Early Rejection ✓

- [ ] Student submits request
- [ ] Faculty Coordinator rejects
- [ ] Status becomes REJECTED
- [ ] Request appears in "Finalized"
- [ ] OD counts are NOT updated

### Test Case 3: Invalid Approval Attempt ✓

- [ ] Student submits request
- [ ] Student Welfare tries to approve
- [ ] Error message displayed: "Cannot approve: Student Welfare can only approve..."
- [ ] Request status remains SUBMITTED

### Test Case 4: Skip Level Attempt ✓

- [ ] Faculty Coordinator approves
- [ ] HOD tries to approve (skipping Student Welfare)
- [ ] Error message displayed
- [ ] Request remains in PENDING_WELFARE_APPROVAL

## Key Improvements

1. **Strict Validation**: No approval level can be skipped
2. **Clear Error Messages**: Users know exactly why they can't act on a request
3. **Audit Trail**: All actions logged with detailed information
4. **Role Clarity**: "Event Coordinator" renamed to "Faculty Coordinator"
5. **Workflow Visibility**: Status descriptions show step numbers (1 of 3, 2 of 3, etc.)
6. **Defensive Programming**: Multiple validation layers prevent workflow violations

## Files Modified

```
src/main/java/in/srmup/odms/
├── model/
│   └── RequestStatus.java                    [MODIFIED]
├── service/
│   └── EventRequestService.java             [MODIFIED]
├── controller/
│   ├── ApproverDashboardController.java     [MODIFIED]
│   └── EventRequestController.java          [MODIFIED]
└── util/
    └── WorkflowHelper.java                  [CREATED]

Documentation:
├── APPROVAL_WORKFLOW.md                     [CREATED]
└── WORKFLOW_FIX_SUMMARY.md                  [CREATED]
```

## Compilation Status

✅ Clean compilation with no errors
✅ All imports resolved
✅ All methods properly implemented
✅ Test compilation successful

## Next Steps for Deployment

1. **Test the workflow** with all three roles
2. **Verify error messages** appear correctly
3. **Check approval history** is properly recorded
4. **Validate dashboard filters** show correct requests
5. **Test rejection** at each approval stage
6. **Verify OD counts** update only on final approval

## Rollback Plan

If issues occur, the core logic changes are in:

- `EventRequestService.approveRequest()` method
- `EventRequestService.rejectRequest()` method

These can be reverted to the previous conditional logic if needed.

## Support Notes

- All workflow validation errors include the current status in the message
- Approval history provides complete audit trail
- Console logs show detailed approval/rejection information
- Error messages guide users to correct actions
