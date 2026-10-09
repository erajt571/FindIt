# FindIt Task List

This task list is derived from the implementation plan and organized into delivery phases and backlog tickets. It is intended to be used as a working execution checklist for a small 3--4 person team.

## Phase 0: Kickoff & decisions

- [x] Confirm MVP scope and product definition
- [x] Decide auth approach and user roles
- [x] Finalize public/private listing policy
- [x] Define location taxonomy and data constraints
- [x] Lock match threshold and date window
- [x] Choose storage provider and hosting model
- [x] Confirm repo conventions and branch strategy
- [x] Approve decision log and resolve architecture blockers

## Phase 1: Foundation

- [x] Set up repository structure and code conventions
- [x] Create environment templates and secrets handling
- [x] Configure CI pipeline for lint, test, and build
- [x] Initialize application skeletons for backend and frontend
- [x] Connect application to database and migrations
- [x] Add health check endpoint and baseline monitoring
- [x] Define consistent API error response format
- [x] Ensure local build works from a clean checkout

## Phase 2: Identity & data model

- [x] Implement authentication flow
- [x] Expose current-user profile endpoint
- [x] Define user roles and authorization rules
- [x] Create database schema and migrations
- [x] Add seed data for development and testing
- [x] Enforce ownership and access policies
- [x] Define API DTOs for user and report data
- [x] Validate auth and DB constraints with automated tests

## Phase 3: Report lifecycle

- [x] Implement report create/detail/edit/status APIs
- [x] Add validation rules for report lifecycle transitions
- [x] Enforce owner-based access checks
- [x] Build browse/search/filter/pagination APIs
- [ ] Add an image-upload pipeline (P1; not implemented)
- [x] Build report management UI screens
- [x] Add report form and detail views
- [x] Ensure refresh preserves user state and filters
- [x] Verify end-user can create and manage reports

## Phase 4: Matching engine

- [x] Build candidate query logic for matching
- [ ] Integrate an external embedding provider (local lexical provider is implemented)
- [x] Define weighted scoring and thresholds
- [x] Persist match results and explanations
- [x] Add deduplication logic for repeated job runs
- [x] Implement retry and failure handling for matching jobs
- [x] Validate ranking quality using deterministic tests
- [x] Confirm matching failure does not block report creation

## Phase 5: Match/recovery flow

- [x] Build match listing and detail views
- [x] Add feedback mechanism for match quality
- [x] Implement verification request flow
- [x] Support report and match state transitions
- [x] Generate user notifications
- [x] Add read/unread state handling
- [x] Verify two-user end-to-end workflow
- [x] Confirm participant-only updates and role boundaries

## Phase 6: Dashboard & moderation

- [x] Build dashboard metrics view
- [x] Add My Reports and notifications screens
- [x] Create minimal admin moderation queue
- [x] Support remove/suspend actions with reason tracking
- [x] Add audit logging for moderation activity
- [x] Validate role-based access to moderated data
- [x] Confirm counts and summaries are accurate

## Phase 7: Hardening & QA

- [ ] Run security tests and vulnerability checks (auth/authorization tests pass; no dependency vulnerability scan run)
- [x] Perform responsive UI validation (browser smoke-tested at a 390px viewport)
- [ ] Check accessibility basics and keyboard support (focus styles exist; manual audit pending)
- [ ] Cover edge cases and error states
- [ ] Run load smoke tests and performance checks
- [ ] Validate backup/restore procedures
- [ ] Review dependencies and license risk
- [ ] Complete release checklist and resolve critical/high issues

## Phase 8: Deploy & demo

- [x] Configure environment variables and local secret handling
- [ ] Deploy frontend, API, and database (blueprint added; hosted deployment requires account access)
- [x] Run all migrations in a clean local PostgreSQL 18 database (hosted validation pending)
- [x] Smoke-test local PostgreSQL-backed health, report search, matching jobs, notifications, and two-user recovery (hosted smoke test pending)
- [x] Seed safe demo data in local/demo profiles
- [x] Document forward-only migration and demo reset guidance
- [x] Document a reproducible local demo path
- [ ] Share production-like URL for stakeholder validation (no hosted URL available)

## Ticket backlog

| ID | Ticket | Priority | Depends on | Acceptance summary |
| --- | --- | --- | --- | --- |
| INF-001 | Repository, code style, env templates, CI skeleton | P0 | --- | Fresh clone builds; secrets excluded |
| INF-002 | Database migrations and seed strategy | P0 | INF-001 | Schema creates from empty DB; repeatable seed |
| INF-003 | Authentication and current-user API | P0 | INF-001/002 | Register/login/logout; server-side role checks |
| INF-004 | Report CRUD and status transitions | P0 | INF-002/003 | Validation, owner checks, correct state rules |
| INF-005 | Browse/search/filter/pagination API | P0 | INF-004 | Filters combine correctly; bounded page size |
| INF-006 | Frontend shell, routing, design system | P0 | INF-001 | Responsive shared layout and error states |
| INF-007 | Report form and item detail UI | P0 | INF-004/006 | Create/detail/edit/status flows work |
| INF-008 | Browse and dashboard UI | P0 | INF-005/006 | Data-driven search and dashboard metrics |
| INF-009 | Matching domain service and deterministic tests | P0 | INF-004 | Correct candidate direction, ranking, thresholds |
| INF-010 | Embedding provider adapter and job retry | P0 | INF-009 | Timeout/retry/fallback behavior documented |
| INF-011 | Persist match suggestions and deduplicate pairs | P0 | INF-002/009 | No duplicate pairs under repeated jobs |
| INF-012 | Verification flow and match feedback | P0 | INF-011 | Only participants can change match state |
| INF-013 | In-app notifications | P0/P1 | INF-011/012 | Deduplicated, user-scoped, mark-read works |
| INF-014 | Image upload pipeline | P1 | INF-007 | Size/type limits, private keys, preview and failure UX |
| INF-015 | Admin moderation and audit log | P1 | INF-003/004 | ADMIN-only actions require reasons and are logged |
| INF-016 | Automated API/UI journey tests | P0 | Core flows | Two-user demo flow automated or documented as repeatable smoke test |
| INF-017 | Deployment, migration, rollback docs | P0 | All P0 | Deployed app passes smoke test |

## Suggested execution order

1. Phase 0 and Phase 1
2. Phase 2 and Phase 3
3. Phase 4 and Phase 5
4. Phase 6 and Phase 7
5. Phase 8 and launch prep

## Ready-to-start task snapshot

- [x] INF-001: Repository and CI setup
- [x] INF-002: Database and migration setup
- [x] INF-003: Authentication and current-user API
- [x] INF-004: Report CRUD and statuses
- [x] INF-005: Search and browse API
- [x] INF-006: Frontend shell and routing
- [x] INF-007: Report UI screens
- [x] INF-009: Matching service and deterministic tests (local lexical provider; external embeddings pending)
- [ ] INF-010: External embedding provider integration
- [x] INF-011: Match persistence and deduplication
- [x] INF-012: Verification flow and feedback
- [x] INF-013: Notifications
- [x] INF-015: Admin moderation
- [x] INF-016: API integration journey tests
- [x] INF-017: Deployment blueprint and local setup/rollback documentation (hosted rollout pending)
