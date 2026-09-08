CREATE TYPE claim_draft_status AS ENUM ('draft', 'deleted');
CREATE TABLE claim_drafts
(
    id                  UUID                   PRIMARY KEY,
    source_system       TEXT                   NOT NULL,
    claim_type_id       UUID                   NULL,
    certificate_id      TEXT                   NULL,
    created_by_user_id  TEXT                   NOT NULL,
    status              claim_draft_status     DEFAULT 'draft' NOT NULL,
    data                JSONB                  DEFAULT '{}'::JSONB NOT NULL,
    metadata            JSONB                  DEFAULT '{}'::JSONB NOT NULL,
    created_at          TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_claim_drafts_claim_type_id ON claim_drafts (claim_type_id);
CREATE INDEX idx_claim_drafts_certificate_id ON claim_drafts (certificate_id);
CREATE INDEX idx_claim_drafts_created_by_user_id ON claim_drafts (created_by_user_id);
CREATE INDEX idx_claim_drafts_status ON claim_drafts (status);
