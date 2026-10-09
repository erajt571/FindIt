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

- [ ] Implement authentication flow
- [ ] Expose current-user profile endpoint
- [ ] Define user roles and authorization rules
- [ ] Create database schema and migrations
- [ ] Add seed data for development and testing
- [ ] Enforce ownership and access policies
- [ ] Define API DTOs for user and report data
- [ ] Validate auth and DB constraints with automated tests

## Phase 3: Report lifecycle

- [ ] Implement report create/detail/edit/status APIs
- [ ] Add validation rules for report lifecycle transitions
- [ ] Enforce owner-based access checks
- [ ] Build browse/search/filter/pagination APIs
- [ ] Support image upload if prioritized as P1
- [ ] Build report management UI screens
- [ ] Add report form and detail views
- [ ] Ensure refresh preserves user state and filters
- [ ] Verify end-user can create and manage reports

## Phase 4: Matching engine

- [ ] Build candidate query logic for matching
- [ ] Integrate embedding provider adapter
- [ ] Define weighted scoring and thresholds
- [ ] Persist match results and explanations
- [ ] Add deduplication logic for repeated job runs
- [ ] Implement retry and failure handling for AI matching
- [ ] Validate ranking quality using deterministic tests
- [ ] Confirm AI failure does not block report creation

## Phase 5: Match/recovery flow

- [ ] Build match listing and detail views
- [ ] Add feedback mechanism for match quality
- [ ] Implement verification request flow
- [ ] Support report and match state transitions
- [ ] Generate user notifications
- [ ] Add read/unread state handling
- [ ] Verify two-user end-to-end workflow
- [ ] Confirm participant-only updates and role boundaries

## Phase 6: Dashboard & moderation

- [ ] Build dashboard metrics view
- [ ] Add My Reports and notifications screens
- [ ] Create minimal admin moderation queue
- [ ] Support remove/suspend actions with reason tracking
- [ ] Add audit logging for moderation activity
- [ ] Validate role-based access to moderated data
- [ ] Confirm counts and summaries are accurate

## Phase 7: Hardening & QA

- [ ] Run security tests and vulnerability checks
- [ ] Perform responsive UI validation
- [ ] Check accessibility basics and keyboard support
- [ ] Cover edge cases and error states
- [ ] Run load smoke tests and performance checks
- [ ] Validate backup/migration procedures
- [ ] Review dependencies and license risk
- [ ] Complete release checklist and resolve critical/high issues

## Phase 8: Deploy & demo

- [ ] Configure secrets and environment variables
- [ ] Deploy frontend, API, and database
- [ ] Run migrations in production-like environment
- [ ] Conduct smoke tests against deployed app
- [ ] Seed safe demo data
- [ ] Document rollback and demo reset process
- [ ] Verify reproducible demo path
- [ ] Share production-like URL for stakeholder validation

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

- [ ] INF-001: Repository and CI setup
- [ ] INF-002: Database and migration setup
- [ ] INF-003: Authentication and current-user API
- [ ] INF-004: Report CRUD and statuses
- [ ] INF-005: Search and browse API
- [ ] INF-006: Frontend shell and routing
- [ ] INF-007: Report UI screens
- [ ] INF-009: Matching service and deterministic tests
- [ ] INF-010: Embedding provider adapter
- [ ] INF-011: Match persistence and deduplication
- [ ] INF-012: Verification flow and feedback
- [ ] INF-013: Notifications
- [ ] INF-015: Admin moderation
- [ ] INF-016: Test automation
- [ ] INF-017: Deployment and rollback documentation
