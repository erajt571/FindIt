# Add validation rules for report lifecycle transitions

**Task group:** Phase 3: Report lifecycle

## Description

Define valid report statuses and allowable transitions using the SRS states ACTIVE, PENDING_VERIFICATION, and RESOLVED. Treat moderation removal as a separate visibility/moderation action rather than an ordinary recovery state.

## Completion outcome

The three report states and their transitions are enforced by the backend; tests cover valid and invalid changes and moderation does not masquerade as recovery.

