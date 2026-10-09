# FindIt task descriptions

Individual descriptions for every unique phase checklist item and backlog ticket in `FindIt_Task_List.md`. The repeated "Ready-to-start task snapshot" is not duplicated here; its items refer to backlog tickets below.

These descriptions use both `FindIt — AI-Powered Campus Lost & Found.md` (the SRS) and `FindIt_Implementation_Plan.md`. They reflect the Next.js client, Spring Boot REST API, PostgreSQL, USER/ADMIN roles, advisory asynchronous AI matching, the SRS initial tunable 60/20/10/10 score weighting, and a persisted recovery workflow. SRS items marked Should Have remain subject to the priorities in the task list and implementation plan.

## Phase tasks

### Phase 0: Kickoff & decisions

- [Confirm MVP scope and product definition](./phase-0-kickoff-decisions/01-confirm-mvp-scope-and-product-definition.md)
- [Decide auth approach and user roles](./phase-0-kickoff-decisions/02-decide-auth-approach-and-user-roles.md)
- [Finalize public/private listing policy](./phase-0-kickoff-decisions/03-finalize-public-private-listing-policy.md)
- [Define location taxonomy and data constraints](./phase-0-kickoff-decisions/04-define-location-taxonomy-and-data-constraints.md)
- [Lock match threshold and date window](./phase-0-kickoff-decisions/05-lock-match-threshold-and-date-window.md)
- [Choose storage provider and hosting model](./phase-0-kickoff-decisions/06-choose-storage-provider-and-hosting-model.md)
- [Confirm repo conventions and branch strategy](./phase-0-kickoff-decisions/07-confirm-repo-conventions-and-branch-strategy.md)
- [Approve decision log and resolve architecture blockers](./phase-0-kickoff-decisions/08-approve-decision-log-and-resolve-architecture-blockers.md)

### Phase 1: Foundation

- [Set up repository structure and code conventions](./phase-1-foundation/01-set-up-repository-structure-and-code-conventions.md)
- [Create environment templates and secrets handling](./phase-1-foundation/02-create-environment-templates-and-secrets-handling.md)
- [Configure CI pipeline for lint, test, and build](./phase-1-foundation/03-configure-ci-pipeline-for-lint-test-and-build.md)
- [Initialize application skeletons for backend and frontend](./phase-1-foundation/04-initialize-application-skeletons-for-backend-and-frontend.md)
- [Connect application to database and migrations](./phase-1-foundation/05-connect-application-to-database-and-migrations.md)
- [Add health check endpoint and baseline monitoring](./phase-1-foundation/06-add-health-check-endpoint-and-baseline-monitoring.md)
- [Define consistent API error response format](./phase-1-foundation/07-define-consistent-api-error-response-format.md)
- [Ensure local build works from a clean checkout](./phase-1-foundation/08-ensure-local-build-works-from-a-clean-checkout.md)

### Phase 2: Identity & data model

- [Implement authentication flow](./phase-2-identity-data-model/01-implement-authentication-flow.md)
- [Expose current-user profile endpoint](./phase-2-identity-data-model/02-expose-current-user-profile-endpoint.md)
- [Define user roles and authorization rules](./phase-2-identity-data-model/03-define-user-roles-and-authorization-rules.md)
- [Create database schema and migrations](./phase-2-identity-data-model/04-create-database-schema-and-migrations.md)
- [Add seed data for development and testing](./phase-2-identity-data-model/05-add-seed-data-for-development-and-testing.md)
- [Enforce ownership and access policies](./phase-2-identity-data-model/06-enforce-ownership-and-access-policies.md)
- [Define API DTOs for user and report data](./phase-2-identity-data-model/07-define-api-dtos-for-user-and-report-data.md)
- [Validate auth and DB constraints with automated tests](./phase-2-identity-data-model/08-validate-auth-and-db-constraints-with-automated-tests.md)

### Phase 3: Report lifecycle

- [Implement report create/detail/edit/status APIs](./phase-3-report-lifecycle/01-implement-report-create-detail-edit-status-apis.md)
- [Add validation rules for report lifecycle transitions](./phase-3-report-lifecycle/02-add-validation-rules-for-report-lifecycle-transitions.md)
- [Enforce owner-based access checks](./phase-3-report-lifecycle/03-enforce-owner-based-access-checks.md)
- [Build browse/search/filter/pagination APIs](./phase-3-report-lifecycle/04-build-browse-search-filter-pagination-apis.md)
- [Support image upload if prioritized as P1](./phase-3-report-lifecycle/05-support-image-upload-if-prioritized-as-p1.md)
- [Build report management UI screens](./phase-3-report-lifecycle/06-build-report-management-ui-screens.md)
- [Add report form and detail views](./phase-3-report-lifecycle/07-add-report-form-and-detail-views.md)
- [Ensure refresh preserves user state and filters](./phase-3-report-lifecycle/08-ensure-refresh-preserves-user-state-and-filters.md)
- [Verify end-user can create and manage reports](./phase-3-report-lifecycle/09-verify-end-user-can-create-and-manage-reports.md)

### Phase 4: Matching engine

- [Build candidate query logic for matching](./phase-4-matching-engine/01-build-candidate-query-logic-for-matching.md)
- [Integrate embedding provider adapter](./phase-4-matching-engine/02-integrate-embedding-provider-adapter.md)
- [Define weighted scoring and thresholds](./phase-4-matching-engine/03-define-weighted-scoring-and-thresholds.md)
- [Persist match results and explanations](./phase-4-matching-engine/04-persist-match-results-and-explanations.md)
- [Add deduplication logic for repeated job runs](./phase-4-matching-engine/05-add-deduplication-logic-for-repeated-job-runs.md)
- [Implement retry and failure handling for AI matching](./phase-4-matching-engine/06-implement-retry-and-failure-handling-for-ai-matching.md)
- [Validate ranking quality using deterministic tests](./phase-4-matching-engine/07-validate-ranking-quality-using-deterministic-tests.md)
- [Confirm AI failure does not block report creation](./phase-4-matching-engine/08-confirm-ai-failure-does-not-block-report-creation.md)

### Phase 5: Match/recovery flow

- [Build match listing and detail views](./phase-5-match-recovery-flow/01-build-match-listing-and-detail-views.md)
- [Add feedback mechanism for match quality](./phase-5-match-recovery-flow/02-add-feedback-mechanism-for-match-quality.md)
- [Implement verification request flow](./phase-5-match-recovery-flow/03-implement-verification-request-flow.md)
- [Support report and match state transitions](./phase-5-match-recovery-flow/04-support-report-and-match-state-transitions.md)
- [Generate user notifications](./phase-5-match-recovery-flow/05-generate-user-notifications.md)
- [Add read/unread state handling](./phase-5-match-recovery-flow/06-add-read-unread-state-handling.md)
- [Verify two-user end-to-end workflow](./phase-5-match-recovery-flow/07-verify-two-user-end-to-end-workflow.md)
- [Confirm participant-only updates and role boundaries](./phase-5-match-recovery-flow/08-confirm-participant-only-updates-and-role-boundaries.md)

### Phase 6: Dashboard & moderation

- [Build dashboard metrics view](./phase-6-dashboard-moderation/01-build-dashboard-metrics-view.md)
- [Add My Reports and notifications screens](./phase-6-dashboard-moderation/02-add-my-reports-and-notifications-screens.md)
- [Create minimal admin moderation queue](./phase-6-dashboard-moderation/03-create-minimal-admin-moderation-queue.md)
- [Support remove/suspend actions with reason tracking](./phase-6-dashboard-moderation/04-support-remove-suspend-actions-with-reason-tracking.md)
- [Add audit logging for moderation activity](./phase-6-dashboard-moderation/05-add-audit-logging-for-moderation-activity.md)
- [Validate role-based access to moderated data](./phase-6-dashboard-moderation/06-validate-role-based-access-to-moderated-data.md)
- [Confirm counts and summaries are accurate](./phase-6-dashboard-moderation/07-confirm-counts-and-summaries-are-accurate.md)

### Phase 7: Hardening & QA

- [Run security tests and vulnerability checks](./phase-7-hardening-qa/01-run-security-tests-and-vulnerability-checks.md)
- [Perform responsive UI validation](./phase-7-hardening-qa/02-perform-responsive-ui-validation.md)
- [Check accessibility basics and keyboard support](./phase-7-hardening-qa/03-check-accessibility-basics-and-keyboard-support.md)
- [Cover edge cases and error states](./phase-7-hardening-qa/04-cover-edge-cases-and-error-states.md)
- [Run load smoke tests and performance checks](./phase-7-hardening-qa/05-run-load-smoke-tests-and-performance-checks.md)
- [Validate backup/migration procedures](./phase-7-hardening-qa/06-validate-backup-migration-procedures.md)
- [Review dependencies and license risk](./phase-7-hardening-qa/07-review-dependencies-and-license-risk.md)
- [Complete release checklist and resolve critical/high issues](./phase-7-hardening-qa/08-complete-release-checklist-and-resolve-critical-high-issues.md)

### Phase 8: Deploy & demo

- [Configure secrets and environment variables](./phase-8-deploy-demo/01-configure-secrets-and-environment-variables.md)
- [Deploy frontend, API, and database](./phase-8-deploy-demo/02-deploy-frontend-api-and-database.md)
- [Run migrations in production-like environment](./phase-8-deploy-demo/03-run-migrations-in-production-like-environment.md)
- [Conduct smoke tests against deployed app](./phase-8-deploy-demo/04-conduct-smoke-tests-against-deployed-app.md)
- [Seed safe demo data](./phase-8-deploy-demo/05-seed-safe-demo-data.md)
- [Document rollback and demo reset process](./phase-8-deploy-demo/06-document-rollback-and-demo-reset-process.md)
- [Verify reproducible demo path](./phase-8-deploy-demo/07-verify-reproducible-demo-path.md)
- [Share production-like URL for stakeholder validation](./phase-8-deploy-demo/08-share-production-like-url-for-stakeholder-validation.md)

## Backlog tickets

- [INF-001: Repository, code style, env templates, CI skeleton](./tickets/inf-001.md)
- [INF-002: Database migrations and seed strategy](./tickets/inf-002.md)
- [INF-003: Authentication and current-user API](./tickets/inf-003.md)
- [INF-004: Report CRUD and status transitions](./tickets/inf-004.md)
- [INF-005: Browse/search/filter/pagination API](./tickets/inf-005.md)
- [INF-006: Frontend shell, routing, design system](./tickets/inf-006.md)
- [INF-007: Report form and item detail UI](./tickets/inf-007.md)
- [INF-008: Browse and dashboard UI](./tickets/inf-008.md)
- [INF-009: Matching domain service and deterministic tests](./tickets/inf-009.md)
- [INF-010: Embedding provider adapter and job retry](./tickets/inf-010.md)
- [INF-011: Persist match suggestions and deduplicate pairs](./tickets/inf-011.md)
- [INF-012: Verification flow and match feedback](./tickets/inf-012.md)
- [INF-013: In-app notifications](./tickets/inf-013.md)
- [INF-014: Image upload pipeline](./tickets/inf-014.md)
- [INF-015: Admin moderation and audit log](./tickets/inf-015.md)
- [INF-016: Automated API/UI journey tests](./tickets/inf-016.md)
- [INF-017: Deployment, migration, rollback docs](./tickets/inf-017.md)
