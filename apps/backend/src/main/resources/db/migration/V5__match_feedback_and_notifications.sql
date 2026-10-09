CREATE TABLE match_feedback (
    id UUID PRIMARY KEY,
    match_id UUID NOT NULL REFERENCES match_records(id),
    user_id UUID NOT NULL REFERENCES users(id),
    feedback VARCHAR(20) NOT NULL CHECK (feedback IN ('RELEVANT', 'NOT_RELEVANT')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_match_feedback_user UNIQUE (match_id, user_id)
);
ALTER TABLE match_records ADD COLUMN verification_requested_by UUID REFERENCES users(id);

ALTER TABLE notifications ADD COLUMN title VARCHAR(160) NOT NULL DEFAULT 'FindIt update';
ALTER TABLE notifications ADD COLUMN notification_type VARCHAR(40) NOT NULL DEFAULT 'GENERAL';
ALTER TABLE notifications ADD COLUMN related_report_id UUID REFERENCES reports(id);
ALTER TABLE notifications ADD COLUMN match_id UUID REFERENCES match_records(id);
ALTER TABLE notifications ADD COLUMN deduplication_key VARCHAR(160);
CREATE UNIQUE INDEX uk_notifications_deduplication_key ON notifications(deduplication_key);
CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at);
