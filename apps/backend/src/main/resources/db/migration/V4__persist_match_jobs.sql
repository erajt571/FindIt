CREATE TABLE match_jobs (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL REFERENCES reports(id),
    state VARCHAR(20) NOT NULL CHECK (state IN ('PENDING', 'RUNNING', 'RETRY', 'COMPLETED', 'FAILED')),
    attempts INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_error_code VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_match_jobs_state_next_attempt ON match_jobs(state, next_attempt_at);
CREATE INDEX idx_match_jobs_report_id ON match_jobs(report_id);
