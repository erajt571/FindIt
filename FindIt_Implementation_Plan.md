FINDIT

AI-Powered Campus Lost & Found

IMPLEMENTATION PLAN & DEVELOPER HANDOFF

Version 1.0 \| 9 October 2026 Basis: FindIt Software Requirements &
Project Specification Audience: Frontend, backend, AI, QA, and
deployment contributors

| Plan at a glance \| Decision \|

| --- \| --- \|

| Delivery target \| A demonstrable MVP with a complete lost-to-found
  recovery workflow \|

| Architecture \| Next.js + Spring Boot REST API + PostgreSQL \|

| AI strategy \| Embeddings + deterministic weighted scoring; matching
  runs asynchronously \|

| Core principle \| Reports persist even if AI matching or notifications
  fail \|

| Out of scope for MVP \| Vision matching, live chat, GPS maps, email
  delivery, advanced analytics \|

Engineering principle: ship a reliable end-to-end workflow before adding
optional integrations.

## 1. Executive Summary

This plan translates the supplied FindIt SRS into an implementation
sequence and technical contract that a small developer team can execute.
The MVP lets authenticated users create lost/found reports, browse and
filter reports, receive ranked potential matches, review suggestions,
manage recovery status, and receive in-app notifications. Administrators
can moderate reports and suspend accounts.

The system must not treat AI scores as proof of ownership. AI matching
is advisory, must be explainable at a basic level, and must fail
independently from report creation.

## 1.1 Delivery outcomes

-   A deployed responsive web app with seeded demo data and documented
    setup instructions.

-   Secure account and role handling, including server-side ownership
    checks.

-   Persisted reports, matches, notifications, and status history
    sufficient for the core flow.

-   A matching service with a documented score, configurable threshold,
    retry handling, and deterministic fallback.

-   Automated tests for key authorization, matching, API, and user
    journeys.

-   A reproducible demo: report lost headphones → matching found report
    → request verification → resolve report.

## 2. Scope and Priority

| Priority \| Included \| Acceptance boundary \|

| --- \| --- \| --- \|

| P0 --- MVP \| Registration/login/logout; report
  create/view/edit/status; browse/search/filter/pagination; matching;
  match suggestions; dashboard; responsive UI; basic in-app
  notifications; deployment \| End-to-end user journey works with
  persistent data and server-enforced authorization \|

| P1 --- If time allows \| Image uploads; admin moderation UI; match
  relevance feedback; report complaints; notification read state \| No
  compromise to P0 reliability or security \|

| P2 --- Later \| Vision-based image similarity; email delivery; campus
  map/GPS; real-time chat; analytics; university SSO; multilingual
  matching \| Do not start before MVP acceptance \|

Scope clarification: the SRS lists image uploads and basic moderation as
"should have," but report photos are optional and moderation is required
for a production campus rollout. For a workshop MVP, implement safe
image handling and minimal moderation only if the core workflow is
complete. Do not represent a frontend-only demo as production-secure.

## 3. Architecture and Technical Decisions

### 3.1 Recommended architecture

| Layer \| Technology \| Responsibilities \|

| --- \| --- \| --- \|

| Web client \| Next.js, React, TypeScript, Tailwind CSS \| Pages,
  forms, responsive UI, client-side validation, API calls,
  loading/error/empty states \|

| REST API \| Spring Boot, Java, Spring Security, Bean Validation \|
  Authentication integration, authorization, business rules, report
  lifecycle, search, matching orchestration, notifications \|

| Relational data \| PostgreSQL \| Users/profile metadata, reports,
  matches, notifications, feedback, moderation and audit records \|

| AI adapter \| Embedding provider behind an interface \| Embedding
  generation, provider timeout/retry, normalized vector similarity; no
  provider-specific logic in controllers \|

| File storage \| Managed object storage such as Cloudinary or
  S3-compatible storage \| Photo upload and delivery; database stores
  only object key/URL and metadata \|

| Hosting/CI \| Render or equivalent; GitHub Actions \| Separate
  frontend/API deployment, environment configuration, migration and test
  pipeline \|

### 3.2 Architecture rules

-   The browser must never connect directly to PostgreSQL or hold
    database credentials.

-   Keep controllers thin. Put rules in services and persistence logic
    in repositories.

-   Define an AI provider interface so the app can switch between real
    embeddings and a local demo matcher.

-   Save the report transaction first; enqueue matching only after
    commit. If no queue is available, use a persisted job table or a
    retryable service process rather than tying report persistence to an
    external API call.

-   Use database migrations from the first schema change; do not rely on
    manual production schema edits.

-   Use UTC timestamps in storage and render dates consistently in the
    user's local timezone.

-   Use a single canonical API base URL configured by environment, not
    hardcoded across components.

### 3.3 Proposed repository layout

Repository may be a monorepo for workshop simplicity:

-   frontend/ --- Next.js app, components, pages/routes, API client,
    tests

-   backend/ --- Spring Boot app, domain entities, DTOs, controllers,
    services, repositories, security, AI adapter

-   backend/src/main/resources/db/migration/ --- versioned SQL
    migrations

-   docs/ --- SRS, implementation plan, API notes, setup, demo checklist

-   .github/workflows/ --- lint, test, build, migration validation

## 4. Decisions That Must Be Locked Before Coding

| Decision \| Recommended default \| Reason / guardrail \|

| --- \| --- \| --- \|

| Authentication \| Spring Security with secure server-managed session
  or short-lived access token plus refresh strategy; decide once and
  document \| Avoid ad-hoc localStorage token handling. If Supabase Auth
  is selected instead, backend must verify its tokens consistently. \|

| Embedding provider \| Provider-neutral adapter; configure one provider
  via environment \| Avoid vendor lock-in and keep secrets server-side.
  \|

| Demo mode \| Separate local/demo profile with seeded accounts and
  deterministic matcher \| Never silently use insecure demo
  authentication in production. \|

| Campus locations \| Controlled location list plus optional free-text
  detail \| Improves matching and filtering; avoids inconsistent
  spelling. \|

| Report edits \| Owner can edit active report; re-run matching after
  relevant fields change \| Old matches can become stale. \|

| Status model \| ACTIVE, PENDING_VERIFICATION, RESOLVED, REMOVED \|
  REMOVED is a moderation/system visibility state; do not confuse it
  with successful recovery. \|

| Match state \| SUGGESTED, ACCEPTED_FOR_VERIFICATION, REJECTED, CLOSED
  \| Avoid using match state and report state interchangeably. \|

| Admin bootstrap \| One-time secure environment/config-based bootstrap;
  never public registration as ADMIN \| Prevents privilege escalation.
  \|

| Image privacy \| Public thumbnail only if product decision permits;
  avoid sensitive details in public images \| ID cards and documents may
  expose personal data. \|

These are implementation decisions proposed to resolve gaps in the SRS.
The product owner should approve them during kickoff. Any change should
be recorded before implementation.

## 5. Data Model and Database Requirements

### 5.1 Core entities

| Entity \| Required fields / behavior \|

| --- \| --- \|

| users \| id UUID; display_name; email unique and normalized;
  password_hash only if local auth is used; role USER/ADMIN;
  account_status ACTIVE/SUSPENDED; created_at; updated_at \|

| item_reports \| id UUID; reporter_id FK; report_type LOST/FOUND;
  item_name; category; description; distinguishing_details; location_id
  or normalized location; location_detail nullable; incident_date;
  image_object_key/image_url nullable; status
  ACTIVE/PENDING_VERIFICATION/RESOLVED/REMOVED; created_at; updated_at;
  version for optimistic concurrency if used \|

| matches \| id UUID; lost_report_id FK; found_report_id FK; score
  0--100; score_version; match_reason JSON/text; status
  SUGGESTED/ACCEPTED_FOR_VERIFICATION/REJECTED/CLOSED; created_at;
  updated_at \|

| notifications \| id UUID; user_id FK; type; title/message;
  related_report_id nullable; match_id nullable; read_at nullable;
  created_at; deduplication key \|

| match_feedback \| id UUID; match_id FK; user_id FK; feedback
  RELEVANT/NOT_RELEVANT; created_at; unique per user/match \|

| moderation_actions \| id UUID; admin_id FK; target type/id; action;
  reason; created_at; append-only audit trail \|

| match_jobs (recommended) \| id UUID; report_id FK; state
  PENDING/RUNNING/RETRY/COMPLETED/FAILED; attempts; next_attempt_at;
  last_error_code; created_at; updated_at \|

### 5.2 Constraints and indexes

-   Unique normalized email; foreign keys on all relationships; NOT NULL
    and CHECK constraints for enumerated states and score range 0--100.

-   Ensure lost_report_id references a LOST report and found_report_id
    references a FOUND report in service validation; consider a
    database-level strategy if enforceable in the selected schema.

-   Unique (lost_report_id, found_report_id) to prevent duplicate match
    records.

-   Index item_reports(status, report_type, created_at DESC), category,
    incident_date, location_id, and reporter_id.

-   Index notifications(user_id, read_at, created_at DESC) and
    match_jobs(state, next_attempt_at).

-   Define deletion behavior deliberately. Prefer soft-removal for
    reports and retain audit history; do not cascade-delete match/audit
    records accidentally.

-   Use a migration tool such as Flyway and add migrations to version
    control.

### 5.3 Data lifecycle and consistency

-   Creating a report and creating a match job should be atomic where
    possible. If an external job queue is added later, use an outbox
    pattern to avoid losing events.

-   A report marked RESOLVED or REMOVED must no longer be offered as a
    new active candidate.

-   If a report's type, description, category, location, or incident
    date changes, invalidate or recompute its existing suggestions.

-   If either side of a match is resolved, removed, or rejected, close
    or suppress the associated suggestion as appropriate.

-   Keep a minimal audit trail for admin actions and important status
    transitions.

## 6. API Contract

Use REST JSON APIs under /api/v1. Return consistent HTTP status codes
and a consistent error body. All protected endpoints must enforce
authorization on the server.

| Method and endpoint \| Purpose \| Access / key behavior \|

| --- \| --- \| --- \|

| POST /auth/register \| Create account \| Public; validate
  email/password; never accept role from user payload \|

| POST /auth/login \| Authenticate \| Public; generic
  invalid-credentials response; rate limit \|

| POST /auth/logout \| End session \| Authenticated; invalidate
  session/refresh token as applicable \|

| GET /users/me \| Current user profile \| Authenticated \|

| GET /reports \| Browse/search/filter reports \| Public read only if
  approved; otherwise authenticated. Supports type, category, location,
  date range, status, q, page, size, sort \|

| POST /reports \| Create report \| Authenticated; return 201 and report
  ID; persist before AI work \|

| GET /reports/{id} \| Report detail \| Apply visibility and privacy
  rules \|

| PATCH /reports/{id} \| Edit report \| Owner or admin; validate
  changes; re-run matching if relevant fields change \|

| PATCH /reports/{id}/status \| Change status \| Owner/admin policy;
  validate allowed state transition \|

| GET /reports/{id}/matches \| List match suggestions \| Authenticated;
  only disclose to relevant report owners and admins \|

| POST /matches/{id}/feedback \| Relevant/not relevant \| Authenticated;
  one feedback per user/match \|

| POST /matches/{id}/verification \| Start verification request \|
  Authenticated; must be related to one of the reports \|

| PATCH /matches/{id}/status \| Accept/reject/close suggestion \| Only
  relevant participants/admin; validate transition \|

| GET /notifications \| List current user's notifications \|
  Authenticated; paginated \|

| PATCH /notifications/{id}/read \| Mark read \| Owner only \|

| POST /uploads/presign or /uploads \| Secure image upload \|
  Authenticated; type/size checks; never trust client MIME alone \|

| GET /admin/reports \| Moderation queue \| ADMIN only \|

| POST /admin/reports/{id}/remove \| Remove listing with reason \| ADMIN
  only; audit action \|

| POST /admin/users/{id}/suspend \| Suspend account with reason \| ADMIN
  only; audit action \|

### 6.1 API conventions

-   List response: { data: \[...\], page, size, totalElements,
    totalPages }.

-   Error response: { code, message, fieldErrors?, traceId? }; never
    return stack traces or secrets.

-   Use 400 for validation errors, 401 for unauthenticated, 403 for
    forbidden, 404 for missing/invisible resources, 409 for state
    conflicts, 429 for rate limits, and 5xx for unexpected failures.

-   Use DTOs, not persistence entities, as public API request/response
    types.

-   Validate page size and cap it (for example, maximum 50). Whitelist
    sort fields.

-   Use idempotency or deduplication for repeated job processing and
    notifications.

## 7. AI Matching Design

### 7.1 Processing flow

1.  After report commit, create a match job. Return report creation
    success without waiting for embedding-provider latency.

2.  Fetch active candidate reports of the opposite report type; exclude
    the same report, removed/resolved reports, and already rejected
    pairs where policy says not to resurface them.

3.  Build a text representation from item name, category, description,
    and distinguishing details. Do not include private user contact
    information.

4.  Generate embeddings through the AI adapter. Cache embeddings by
    normalized text plus model/version where practical.

5.  Compute semantic similarity and structured metadata scores; apply
    hard eligibility rules before ranking.

6.  Persist top-ranked candidates and the score version. Create
    deduplicated notifications only when a candidate crosses the
    configured threshold or materially improves.

7.  Mark the job complete. On provider timeout/rate limit, retry with
    bounded exponential backoff; after the retry limit, mark failed and
    expose a retry/admin mechanism.

8.  When an affected report changes, recompute candidates and
    close/suppress stale suggestions.

### 7.2 Score definition

Initial score: 0.60 × semantic description/name similarity + 0.20 ×
category compatibility + 0.10 × location similarity + 0.10 × date
proximity. Convert each component to a normalized 0--1 value before
weighting; multiply the result by 100 for display.

| Component \| Initial implementation rule \|

| --- \| --- \|

| Semantic similarity \| Cosine similarity of embeddings,
  transformed/clamped to a normalized 0--1 range based on the chosen
  model's observed behavior; document the transform \|

| Category \| Exact category = 1; compatible parent/child category =
  configurable partial score; incompatible category = candidate
  exclusion or 0 \|

| Location \| Same controlled location = 1; nearby/related campus
  locations = partial score from a configurable location map; unknown
  location = neutral/low-confidence policy \|

| Date \| Configurable decay by absolute day difference; choose and
  document a maximum matching window rather than assuming all dates
  remain eligible \|

The 60/20/10/10 weights come from the SRS as initial design choices, not
measured truth. Make weights, date window, and minimum threshold
configuration values. Do not label a score as a probability. Tune
against a manually reviewed set of representative lost/found pairs.

### 7.3 AI safety and failure handling

-   Never let AI decide ownership or automatically mark an item
    resolved.

-   Descriptions and uploaded text are untrusted input; do not treat
    report content as instructions to the model.

-   Do not send emails, phone numbers, authentication data, or unrelated
    personal data to the embedding provider.

-   Set request timeouts, rate limits, bounded retries, and provider
    error logging without storing secrets or full sensitive payloads.

-   Use a deterministic local matcher as a demo/fallback only when
    explicitly configured; show a small "demo matching" indicator if
    real embeddings are not active.

-   If the provider is down, report creation and browsing remain
    available; show matching as pending or temporarily unavailable.

-   Add tests for semantically similar descriptions with different
    wording and hard negatives that share keywords but refer to
    different items.

## 8. Authentication, Authorization, Privacy, and Abuse Controls

### 8.1 Authorization matrix

| Action \| Guest \| Authenticated user \| Admin \|

| --- \| --- \| --- \| --- \|

| Browse public active listings \| If product owner approves \| Yes \|
  Yes \|

| Create report \| No \| Yes \| Yes \|

| Edit/delete own report \| No \| Yes, own only \| Yes, with audit for
  moderation \|

| Edit another user's report \| No \| No \| Yes, policy-controlled \|

| View private match details \| No \| Only if participant in relevant
  report/match \| Yes \|

| Mark own notification read \| No \| Own only \| Own only \|

| Suspend user/remove report \| No \| No \| Yes, reason required and
  audited \|

### 8.2 Security checklist

-   Use a maintained authentication framework. Hash passwords with
    Argon2id or bcrypt if passwords are stored locally.

-   Prefer secure HttpOnly, Secure, SameSite cookies for browser
    sessions; apply CSRF protections when cookie authentication is used.

-   Rate-limit login, registration, report creation, verification
    requests, and upload endpoints.

-   Validate all input on the server; escape output; enforce request
    size limits.

-   Restrict upload file types, file size, and image dimensions;
    generate storage keys server-side; scan or re-encode images where
    supported.

-   Do not publish ID-card numbers, phone numbers, addresses, or
    sensitive identifying details. Warn users not to put secret
    ownership proof in public descriptions.

-   Require a claim/verification workflow; do not reveal hidden
    proof-of-ownership details publicly.

-   Use CORS allowlists, security headers, HTTPS, secret
    manager/environment variables, and least-privilege database
    credentials.

-   Log security-relevant actions with a trace ID; never log passwords,
    tokens, or API keys.

-   Test direct-object-reference attacks by trying to access and modify
    another user's report, match, and notifications.

## 9. Frontend Implementation Plan

| Area \| Implementation requirements \|

| --- \| --- \|

| Design system \| Define color tokens, typography, spacing, buttons,
  badges, cards, forms, modal/dialog, toast, skeleton, empty/error state
  components before duplicating UI. \|

| Routing \| Implement all SRS routes with protected-route UX; remember
  that client-side route guards do not replace backend authorization. \|

| API client \| One typed API client with base URL, authentication
  behavior, timeout/error mapping, and request IDs; avoid scattered raw
  fetch calls. \|

| Forms \| Client validation for user experience plus server validation
  as authority; show field-level errors and prevent double-submit. \|

| Browse/search \| URL-backed query parameters for filters and
  pagination so state survives refresh and can be shared. \|

| Dashboard \| Derive metrics from backend data; do not hardcode totals
  in production mode. \|

| Match cards \| Show score, label, shared details, date/location, and a
  clear disclaimer that a suggestion is not proof. \|

| Accessibility \| Keyboard-accessible controls, visible focus, semantic
  headings/labels, alt text, adequate contrast, and status not conveyed
  by color alone. \|

| Responsive behavior \| Test 360px mobile width, tablet, and desktop;
  avoid horizontal overflow and inaccessible modal forms. \|

## 10. Delivery Phases and Work Breakdown

The estimates below assume a small team of approximately 3--4
contributors with frontend, backend, and QA responsibilities. They are
planning estimates, not commitments; revise after a short technical
spike. Work should be tracked as tickets with owners, dependencies,
acceptance criteria, and test evidence.

| Phase \| Tasks \| Exit gate \|

| --- \| --- \| --- \|

| 0. Kickoff & decisions \| Confirm MVP scope, auth approach,
  public/private listing policy, location taxonomy, match threshold/date
  window, storage provider, hosting, roles, repo conventions \| Decision
  log approved; no unresolved architecture blocker \|

| 1. Foundation \| Repo, branching, environment examples, CI,
  lint/test/build, Spring Boot skeleton, Next.js skeleton, DB
  connection, migrations, health endpoint, error format \| Clean
  checkout can build and run locally \|

| 2. Identity & data model \| Auth, profile endpoint, roles,
  schema/migrations, seed data, ownership policy, API DTOs \| Auth tests
  and DB constraints pass \|

| 3. Report lifecycle \| Create/detail/edit/status APIs and UI,
  validation, browse/search/filter/pagination, image upload if P1 \|
  User can create and manage reports; refresh preserves state \|

| 4. Matching engine \| Candidate query, embedding adapter, weighted
  score, thresholds, match persistence, deduplication, retry job,
  explanations \| Test dataset produces plausible rankings; AI failure
  doesn't block report creation \|

| 5. Match/recovery flow \| Match listing/detail, feedback, verification
  request, report/match state transitions, notification generation/read
  state \| Two-user end-to-end workflow passes \|

| 6. Dashboard & moderation \| Dashboard metrics, My Reports,
  notifications page, minimal admin queue, remove/suspend with audit \|
  Role boundaries and data counts verified \|

| 7. Hardening & QA \| Security tests, responsive/accessibility checks,
  edge cases, error states, load smoke test, backup/migration check,
  dependency review \| Release checklist passes; no unresolved
  critical/high issues \|

| 8. Deploy & demo \| Configure secrets, deploy frontend/API/DB, run
  migrations, smoke tests, seed safe demo data, document rollback and
  demo reset \| Production-like URL works; demo path reproducible \|

## 11. Ticket-Level Backlog

| ID \| Ticket \| Priority \| Depends on \| Acceptance summary \|

| --- \| --- \| --- \| --- \| --- \|

| INF-001 \| Repository, code style, env templates, CI skeleton \| P0 \|
  --- \| Fresh clone builds; secrets excluded \|

| INF-002 \| Database migrations and seed strategy \| P0 \| INF-001 \|
  Schema creates from empty DB; repeatable seed \|

| INF-003 \| Authentication and current-user API \| P0 \| INF-001/002 \|
  Register/login/logout; server-side role checks \|

| INF-004 \| Report CRUD and status transitions \| P0 \| INF-002/003 \|
  Validation, owner checks, correct state rules \|

| INF-005 \| Browse/search/filter/pagination API \| P0 \| INF-004 \|
  Filters combine correctly; bounded page size \|

| INF-006 \| Frontend shell, routing, design system \| P0 \| INF-001 \|
  Responsive shared layout and error states \|

| INF-007 \| Report form and item detail UI \| P0 \| INF-004/006 \|
  Create/detail/edit/status flows work \|

| INF-008 \| Browse and dashboard UI \| P0 \| INF-005/006 \| Data-driven
  search and dashboard metrics \|

| INF-009 \| Matching domain service and deterministic tests \| P0 \|
  INF-004 \| Correct candidate direction, ranking, thresholds \|

| INF-010 \| Embedding provider adapter and job retry \| P0 \| INF-009
  \| Timeout/retry/fallback behavior documented \|

| INF-011 \| Persist match suggestions and deduplicate pairs \| P0 \|
  INF-002/009 \| No duplicate pairs under repeated jobs \|

| INF-012 \| Verification flow and match feedback \| P0 \| INF-011 \|
  Only participants can change match state \|

| INF-013 \| In-app notifications \| P0/P1 \| INF-011/012 \|
  Deduplicated, user-scoped, mark-read works \|

| INF-014 \| Image upload pipeline \| P1 \| INF-007 \| Size/type limits,
  private keys, preview and failure UX \|

| INF-015 \| Admin moderation and audit log \| P1 \| INF-003/004 \|
  ADMIN-only actions require reasons and are logged \|

| INF-016 \| Automated API/UI journey tests \| P0 \| Core flows \|
  Two-user demo flow automated or documented as repeatable smoke test \|

| INF-017 \| Deployment, migration, rollback docs \| P0 \| All P0 \|
  Deployed app passes smoke test \|

## 12. Testing Strategy

### 12.1 Test layers

| Layer \| What to test \| Examples \|

| --- \| --- \| --- \|

| Unit \| Pure logic and state transitions \| Score math, date decay,
  category compatibility, allowed transitions, validation rules \|

| Repository/integration \| DB constraints, transactions, query
  correctness \| Unique email, duplicate match prevention, pagination,
  status filtering \|

| API/security \| Authentication, authorization, validation, error
  contract \| Non-owner PATCH returns 403/404; admin endpoints reject
  USER \|

| AI evaluation \| Ranking quality on labelled examples \| Positive
  semantic paraphrases rank above hard negatives; threshold does not
  spam weak candidates \|

| Frontend component \| Form behavior, filter state, loading/error
  states \| Invalid form shows errors; filters update results; toast
  appears on success \|

| End-to-end \| Full user journey in realistic browser flow \| User A
  reports lost item; User B reports found item; match appears; status
  resolves \|

| Operational \| Deployment health and recovery \| Health endpoint,
  migrations, missing AI key, provider timeout, DB unavailable \|

### 12.2 Minimum required test scenarios

-   A lost report is never matched to another lost report; a found
    report is never matched to another found report.

-   Resolved/removed reports are excluded from new candidate results.

-   A low-score candidate does not trigger a user notification.

-   Reprocessing a job does not create duplicate matches or
    notifications.

-   A report remains saved if the embedding provider times out or
    returns an error.

-   Changing match-relevant report fields causes recomputation or
    invalidates stale suggestions.

-   Users cannot read restricted match data or mutate another user's
    reports.

-   Suspended users cannot create new reports or initiate verification.

-   Invalid date, oversized image, unsupported MIME type, empty required
    field, and excessive page size are rejected.

-   Two users can complete the verification flow without the AI
    automatically claiming ownership.

-   A report marked resolved disappears from active browse results but
    remains available to its owner where policy permits.

-   Mobile pages have no horizontal overflow; keyboard users can operate
    forms, dialogs, and navigation.

## 13. Non-Functional Targets and Observability

| Area \| Initial target / requirement \|

| --- \| --- \|

| API performance \| Agree a baseline; target p95 under 500 ms for
  ordinary database-backed list/detail operations under a small demo
  load, excluding AI-provider latency \|

| AI latency \| Matching is asynchronous; UI shows pending state. Set
  provider timeout and bounded retry policy in config. \|

| Availability behavior \| Browse and report CRUD should remain usable
  when the AI provider is down. \|

| Logging \| Structured logs with request/trace ID, endpoint, outcome,
  duration, and safe error code. \|

| Metrics \| Report creation success/failure, match-job backlog/failure
  rate, provider latency/errors, match acceptance/rejection,
  notification creation. \|

| Data protection \| Back up database before release; document retention
  and removal policy; restrict production data access. \|

| Accessibility \| Keyboard navigation, semantic labels, focus
  visibility, contrast, and non-color status cues. \|

| Browser support \| Current major Chrome, Edge, Firefox, and Safari;
  verify target audience's common mobile browser. \|

These are proposed engineering targets, not values specified in the SRS.
Confirm them with the product owner and hosting limits before treating
them as contractual SLAs.

## 14. Deployment and Environment Management

### 14.1 Environments

-   Local: developer-specific environment variables, local PostgreSQL or
    container, seeded non-sensitive data, demo matcher optionally
    enabled.

-   Preview/staging: production-like configuration, separate database
    and storage bucket, test accounts, no real personal data.

-   Production/demo release: managed secrets, HTTPS, restricted admin
    bootstrap, backups, monitored health checks, controlled migrations.

### 14.2 Environment variables

| Variable \| Purpose / rule \|

| --- \| --- \|

| DATABASE_URL / DB_HOST, DB_NAME, DB_USER, DB_PASSWORD \| Database
  connection; secrets only in environment/secret manager \|

| AUTH_SECRET or session configuration \| Signing/session configuration;
  strong random value per environment \|

| AI_PROVIDER, AI_API_KEY, AI_MODEL \| AI adapter config; API key
  server-side only \|

| AI_MATCH_THRESHOLD, AI_DATE_WINDOW_DAYS, AI_SCORE_WEIGHTS \| Tunable
  matching policy; validate on startup \|

| OBJECT_STORAGE\_\* \| Optional upload credentials/bucket settings;
  never expose private credentials to browser \|

| FRONTEND_ORIGIN / CORS_ALLOWED_ORIGINS \| Explicit trusted origins \|

| APP_ENV / DEMO_MODE \| Explicit environment mode; production must
  refuse unsafe demo auth \|

| LOG_LEVEL \| Environment-specific logging without sensitive payloads
  \|

### 14.3 Release/rollback

-   CI must run formatting/lint, unit tests, integration tests, and
    production builds before merge/release.

-   Deploy backward-compatible database migrations before or alongside
    application code; avoid destructive migrations in the same release
    as dependent code changes.

-   Run health checks and smoke tests after deploy: login, browse,
    create report, match job, notification, status change.

-   Keep the previous known-good release available and document
    application rollback. Database rollback must be planned separately;
    prefer forward-fix migrations for destructive changes.

-   Provide a demo reset/seed command that cannot run against production
    accidentally.

## 15. Team Roles and Collaboration

| Role \| Primary ownership \| Review responsibilities \|

| --- \| --- \| --- \|

| Tech lead / integrator \| Architecture decisions, ticket slicing, API
  contract, integration, release readiness \| Cross-layer design,
  security-sensitive changes, scope control \|

| Frontend developer \| Next.js layout, routes, forms, browsing,
  dashboard, notifications, responsive/accessibility \| UI tests, API
  contract adherence, no fake production state \|

| Backend developer \| Spring Boot APIs, auth, entities, repositories,
  migrations, authorization, status rules \| Integration/security tests,
  query performance, transaction correctness \|

| AI/integration owner \| Embedding adapter, scoring, job retry,
  evaluation dataset, match explanation \| Provider failure behavior,
  score versioning, no unsupported confidence claims \|

| QA / rotating reviewer \| Test plan, end-to-end flows, regression
  checklist, defect tracking \| Reproduce bugs and verify fixes before
  release \|

For a 3-person team, combine Tech Lead with Backend, combine AI with
Backend initially, and assign one teammate as rotating QA. Each ticket
still needs one directly responsible owner and one reviewer.

### 15.1 Team workflow

-   Use short-lived branches and pull requests; avoid multiple people
    editing the same core files without coordination.

-   Every PR must state scope, screenshots/API examples where relevant,
    tests run, migration impact, and security implications.

-   Merge only after CI passes and at least one teammate reviews.

-   Track blockers and decisions in the issue tracker; do not silently
    change API payloads or enum values.

-   Use a shared API contract and seeded demo scenario to integrate
    frontend/backend early, not at the end.

## 16. Risks and Mitigations

| Risk \| Impact \| Mitigation \|

| --- \| --- \| --- \|

| AI provider unavailable or rate-limited \| No matches or delayed
  matches \| Persist first, async job, bounded retry, visible pending
  state, deterministic demo fallback \|

| Weak or misleading matches \| Users lose trust or contact wrong person
  \| Hard eligibility rules, threshold, score explanation, feedback,
  labelled evaluation examples \|

| Privacy exposure in descriptions/photos \| Sensitive information leaks
  \| User guidance, restricted fields, upload controls, moderation, safe
  defaults for public listings \|

| Unauthorized report/match mutation \| Abuse or false resolution \|
  Server-side ownership checks, authorization tests, audit trail \|

| Scope creep during workshop \| Core workflow remains unfinished \|
  Lock P0 scope; defer vision, maps, email, chat, analytics \|

| Frontend/backend integration late \| Broken demo near deadline \|
  Agree API contracts early; use mocks only temporarily; integrate one
  vertical slice first \|

| Duplicate matches/notifications \| Spam and inconsistent state \|
  Unique constraints, idempotent job processing, notification dedupe key
  \|

| Stale suggestions after edits/status changes \| Incorrect
  recommendations \| Invalidate/recompute affected pairs and suppress
  inactive reports \|

| Demo code accidentally deployed as secure auth \| Security exposure \|
  Separate demo profile; fail startup in production if unsafe flags are
  enabled \|

| Image upload abuse or broken links \| Security/performance issue \|
  Storage limits, server-side validation, controlled bucket permissions,
  graceful fallback \|

## 17. Definition of Ready and Definition of Done

### 17.1 Definition of Ready

-   Ticket has one owner, priority, scope, dependencies, and testable
    acceptance criteria.

-   Relevant API and data contract are agreed; design has responsive
    states where applicable.

-   Security/privacy implications and failure states have been
    considered.

-   External provider requirements and environment variables are known.

-   Ticket is small enough to review and integrate without a long-lived
    branch.

### 17.2 Definition of Done

-   Acceptance criteria pass and code is merged through review.

-   Unit/integration/security tests relevant to the change pass.

-   Loading, error, empty, and success states are implemented where
    relevant.

-   Authorization is enforced on the server, not only hidden in the UI.

-   Database migrations and environment documentation are committed if
    needed.

-   No secret values or personal data are committed to source
    control/logs.

-   Responsive behavior is checked for affected pages.

-   Feature works against shared test data and does not break the
    end-to-end demo.

-   Known limitations are documented; no important button is a dead end.

## 18. Product Acceptance Checklist

-   [ ] Registration, login, logout, and current-user state work.

-   [ ] A user can create LOST and FOUND reports with required
    validation.

-   [ ] Report listings support text search, type/category/location/date
    filters, pagination, and sorting.

-   [ ] A new report triggers matching asynchronously after persistence.

-   [ ] Matching only compares opposite report types and excludes
    inactive candidates.

-   [ ] Potential matches show score, explanation, and advisory
    disclaimer.

-   [ ] Users can provide match feedback and initiate verification.

-   [ ] Notifications are user-scoped, deduplicated, and can be marked
    read.

-   [ ] Report owners can update allowed statuses; unauthorized changes
    are rejected.

-   [ ] Admin moderation is role-protected and audited if included in
    the release.

-   [ ] AI provider failure does not lose reports or break browsing.

-   [ ] Core data persists across refresh and deployment restarts.

-   [ ] Mobile and desktop layouts are usable and accessible.

-   [ ] Database migrations, setup instructions, environment template,
    and demo steps are documented.

-   [ ] The demo scenario succeeds from a clean seeded state.

## 19. Demo Runbook

1.  Reset to a known demo dataset in a non-production environment.

2.  Sign in as Student A and create a LOST report for black wireless
    headphones near the library on a chosen date.

3.  Sign in as Student B and create a FOUND report with semantically
    similar wording, compatible category, nearby location, and similar
    date.

4.  Wait for the matching job or trigger the demo matcher; confirm one
    ranked candidate is shown.

5.  Inspect the match score and explanation; verify that the UI does not
    call it proof of ownership.

6.  Initiate verification and confirm relevant notifications appear for
    the correct users.

7.  Update the report to RESOLVED as an authorized user and verify it
    leaves active browse results.

8.  Refresh the page and confirm data and status persist.

9.  Optionally simulate an AI-provider failure and show that the report
    remains saved and the match job can retry.

## 20. Open Questions for Product Owner

The SRS does not define the following. Decide and record answers before
the affected features are finalized:

-   Are active item listings publicly viewable without login, or must
    users sign in to browse?

-   Should only university email addresses be allowed? If so, which
    domains are valid?

-   What is the campus location taxonomy, and can users enter free text?

-   What is the maximum report age/date window for matching?

-   What score threshold triggers a visible suggestion and a
    notification?

-   Who may move a report to RESOLVED, and can the other participant
    confirm return?

-   Can reporters edit reports after a match has been accepted for
    verification?

-   What information is public, what is visible only to participants,
    and what should be hidden to prevent false claims?

-   What is the retention/deletion policy for resolved reports, images,
    suspended accounts, and audit records?

-   Which embedding provider and hosting plan are available, and what is
    the expected cost/rate limit?

-   Is the initial delivery a workshop demo or a real campus deployment?
    Real deployment requires a stronger privacy/moderation review.

## 21. Final Engineering Recommendation

Build one vertical slice first: authenticated user → create report →
persist to PostgreSQL → queue matching → show candidate → request
verification → notify participants → resolve report. Then expand browse
filters, dashboard polish, and moderation. This sequence exposes
architecture and integration problems early and produces a demonstrable
product before optional features consume time.

The SRS's original 60/20/10/10 matching weights should remain
configurable and be evaluated against representative examples. Do not
claim measured AI accuracy until the team has built and reviewed a
labelled test set.

This plan is ready to be converted into sprint tickets after the product
owner approves the open decisions in Section 20 and the team confirms
its actual capacity.
