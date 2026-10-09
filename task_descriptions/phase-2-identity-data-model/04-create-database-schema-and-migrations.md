# Create database schema and migrations

**Task group:** Phase 2: Identity & data model

## Description

Model the SRS entities and fields for users, item reports, matches, and notifications, plus only the feedback, moderation, and audit records included in scope. Use USER/ADMIN roles; LOST/FOUND report types; ACTIVE, PENDING_VERIFICATION, and RESOLVED report states; and SUGGESTED, CONFIRMED, REJECTED, and CLOSED match states. Add foreign keys, useful indexes, and a uniqueness constraint for each lost/found pair.

## Completion outcome

A new database can be migrated to the current schema, SRS enum values and relationships are constrained, and duplicate match pairs are prevented.
