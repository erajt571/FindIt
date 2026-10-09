CREATE TABLE moderation_actions (
    id UUID PRIMARY KEY,
    admin_id UUID NOT NULL REFERENCES users(id),
    target_type VARCHAR(20) NOT NULL CHECK (target_type IN ('REPORT', 'USER')),
    target_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('REMOVE', 'SUSPEND', 'RESTORE', 'REACTIVATE')),
    reason VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_moderation_actions_target ON moderation_actions(target_type, target_id);

CREATE TABLE report_status_history (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL REFERENCES reports(id),
    actor_id UUID NOT NULL REFERENCES users(id),
    old_status VARCHAR(30) NOT NULL,
    new_status VARCHAR(30) NOT NULL,
    reason VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_report_status_history_report ON report_status_history(report_id, created_at);
