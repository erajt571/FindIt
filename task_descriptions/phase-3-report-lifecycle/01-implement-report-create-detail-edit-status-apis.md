# Implement report create/detail/edit/status APIs

**Task group:** Phase 3: Report lifecycle

## Description

Build API operations to create a LOST or FOUND report, retrieve its permitted detail, edit allowed fields, and request supported status changes (ACTIVE, PENDING_VERIFICATION, RESOLVED). Validate item name, category, description, campus location, and incident date; accept an optional photograph and notes. Persist the report before triggering asynchronous matching.

## Completion outcome

Authenticated users can complete the supported report lifecycle through the API, required SRS fields are validated, and the persisted report remains available even if matching fails.
