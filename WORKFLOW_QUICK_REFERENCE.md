# Approval Workflow - Quick Reference Guide

## Workflow Overview

```
┌─────────────┐
│  SUBMITTED  │ ← Student submits request
└──────┬──────┘
       │
       ▼ Faculty Coordinator approves
┌──────────────────────────────┐
│ PENDING_WELFARE_APPROVAL    │
└──────────────┬───────────────┘
               │
               ▼ Student Welfare approves
┌──────────────────────────────┐
│ PENDING_HOD_APPROVAL        │
└──────────────┬───────────────┘
               │
               ▼ HOD approves
┌──────────────────────────────┐
│ APPROVED (OD counts updated) │
└──────────────────────────────┘

Note: Request can be REJECTED at any stage by the designated approver.
```

## Role-Based Actions Matrix

| Status                   | Faculty Coordinator  | Student Welfare      | HOD                  | Admin         |
|--------------------------|----------------------|----------------------|----------------------|---------------|
| SUBMITTED                | ✅ Can Approve/Reject | ❌ Cannot Act         | ❌ Cannot Act         | ✅ Can Act     |
| PENDING_WELFARE_APPROVAL | ❌ Cannot Act         | ✅ Can Approve/Reject | ❌ Cannot Act         | ✅ Can Act     |
| PENDING_HOD_APPROVAL     | ❌ Cannot Act         | ❌ Cannot Act         | ✅ Can Approve/Reject | ✅ Can Act     |
| APPROVED                 | 👁️ View Only        | 👁️ View Only        | 👁️ View Only        | 👁️ View Only |
| REJECTED                 | 👁️ View Only        | 👁️ View Only        | 👁️ View Only        | 👁️ View Only |

## Common Error Messages

### ❌ "Faculty Coordinator can only approve requests in SUBMITTED status"

**Meaning:** You're trying to approve a request that's already been approved by you and is waiting for the next
approver.

**Solution:** This request has moved to the next approval stage. Check the "In Progress" section to see its current
status.

---

### ❌ "Student Welfare can only approve requests in PENDING_WELFARE_APPROVAL status"

**Meaning:** The request is either:

- Still waiting for Faculty Coordinator approval (SUBMITTED)
- Already approved by you and waiting for HOD (PENDING_HOD_APPROVAL)

**Solution:** Wait for the previous approver or check "In Progress" for requests you've already approved.

---

### ❌ "HOD can only approve requests in PENDING_HOD_APPROVAL status"

**Meaning:** The request hasn't reached the HOD approval stage yet.

**Solution:** The request is still with Faculty Coordinator or Student Welfare. It will appear in your "Pending My
Action" once they approve it.

---

### ❌ "User with role X cannot reject requests in status Y"

**Meaning:** You can only reject requests that are waiting for YOUR approval.

**Solution:** You cannot reject requests at other approval stages.

## Dashboard Sections Explained

### 📋 Pending My Action

Shows requests waiting for YOUR approval at YOUR stage.

- **Faculty Coordinator:** SUBMITTED requests
- **Student Welfare:** PENDING_WELFARE_APPROVAL requests
- **HOD:** PENDING_HOD_APPROVAL requests

### ⏳ In Progress

Shows requests you've already approved that are waiting for subsequent approvers.

- **Faculty Coordinator:** PENDING_WELFARE_APPROVAL + PENDING_HOD_APPROVAL
- **Student Welfare:** PENDING_HOD_APPROVAL
- **HOD:** Empty (no further approvals needed)

### ✅ Finalized

Shows all APPROVED and REJECTED requests (visible to all roles).

## Status Descriptions

| Status Code              | User-Friendly Description                            |
|--------------------------|------------------------------------------------------|
| SUBMITTED                | "Pending Faculty Coordinator Approval (Step 1 of 3)" |
| PENDING_WELFARE_APPROVAL | "Pending Student Welfare Approval (Step 2 of 3)"     |
| PENDING_HOD_APPROVAL     | "Pending HOD Approval (Step 3 of 3)"                 |
| APPROVED                 | "Fully Approved"                                     |
| REJECTED                 | "Rejected"                                           |

## Best Practices

### ✅ DO:

- Check "Pending My Action" for requests waiting for you
- Review approval history before acting on a request
- Provide clear, specific rejection reasons
- Act promptly to keep workflow moving
- Check request details before approving

### ❌ DON'T:

- Try to approve requests at the wrong stage
- Skip approval levels (system prevents this)
- Approve without reviewing participant details
- Reject without providing a reason

## Workflow Facts

1. **Sequential Only:** Approvals must happen in order - no skipping levels
2. **One Direction:** Once approved at a level, cannot go back
3. **Rejection is Final:** Rejected requests cannot be reopened (need new submission)
4. **OD Counts:** Only updated when request reaches APPROVED status
5. **Audit Trail:** Every action is logged with timestamp and user details
6. **Role-Based:** Each role can only act at their designated stage

## Quick Troubleshooting

**Q: Why can't I see any pending requests?**
A: Either there are no requests at your approval stage, or you're looking at the wrong section. Check all three
dashboard sections.

**Q: I approved a request but it's not in "Finalized"**
A: The request is in "In Progress" waiting for the next approver. It will only reach "Finalized" when fully approved or
rejected.

**Q: Can I undo an approval?**
A: No. Once approved, the request moves to the next stage. Contact admin if needed.

**Q: The request shows wrong status**
A: Check the approval history to see all actions taken. Status should reflect the latest approval.

**Q: Can I approve multiple requests at once?**
A: Currently no. Each request must be reviewed and approved individually.

## Need Help?

- Check `APPROVAL_WORKFLOW.md` for detailed documentation
- Review approval history on the request details page
- Contact system administrator for role/permission issues
- Check console logs for detailed error information (developers)

## Role Display Names

- `ROLE_EVENT_COORDINATOR` = **Faculty Coordinator**
- `ROLE_STUDENT_WELFARE` = **Student Welfare**
- `ROLE_HOD` = **Head of Department**
- `ROLE_ADMIN` = **Administrator**

---

**Last Updated:** [Workflow Fix Implementation]  
**Version:** 1.0
