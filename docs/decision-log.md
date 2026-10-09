# FindIt decision log

## Phase 0 decisions

### 1. MVP scope
The initial release focuses on authenticated users creating and managing lost/found reports, browsing active listings, and receiving AI-assisted match suggestions. Image upload and advanced moderation remain optional follow-ons.

### 2. Authentication and roles
The app uses server-managed authentication with role-based access control. User roles are `USER` and `ADMIN`. The backend enforces authorization on protected endpoints.

### 3. Public/private listing policy
Reports are stored in a controlled, role-aware system with private owner actions and public discovery for active reports. Personal contact details remain hidden from broad public views.

### 4. Location taxonomy
Campus locations are stored as a controlled category list plus optional free-text detail to preserve precision while improving matching quality and filtering.

### 5. Match threshold and date window
The initial matching model uses a weighted score with semantic similarity, category, location, and date proximity. Matching is advisory and does not establish ownership.

### 6. Storage and hosting
PostgreSQL is the canonical relational datastore and the frontend/backend are separated across a monorepo. Local development uses Docker Compose; deployment can later target a managed hosting service.

### 7. Repo conventions and branch strategy
The repo follows a lightweight monorepo structure with a named `main` branch and pull-request-based review flow. Each change should include code quality checks before merge.

### 8. Architecture blockers
The team resolved the main blockers upfront by keeping AI provider logic behind an adapter, separating persistence and matching concerns, and ensuring report creation does not depend on a successful AI call.

## Implementation guardrails

- Keep AI matching advisory, not authoritative.
- Persist reports before asynchronous matching work is attempted.
- Use environment variables for secrets and provider credentials.
- Keep API response formats consistent for frontend consumption.
- Ensure local builds work from a clean checkout with minimal manual setup.
