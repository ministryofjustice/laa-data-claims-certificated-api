CREATE TYPE draft_claim_status AS ENUM ('draft', 'deleted');
CREATE TABLE draft_claims
(
    id                  UUID                   PRIMARY KEY,
    source_system       TEXT                   NOT NULL,
    draft_type_id       UUID                   NULL,
    certificate_id      TEXT                   NULL,
    created_by_user_id  TEXT                   NOT NULL,
    status              draft_claim_status     DEFAULT 'draft' NOT NULL,
    data                JSONB                  DEFAULT '{}'::JSONB NOT NULL,
    metadata            JSONB                  DEFAULT '{}'::JSONB NOT NULL,
    created_at          TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_draft_claims_draft_type_id ON draft_claims (draft_type_id);
CREATE INDEX idx_draft_claims_certificate_id ON draft_claims (certificate_id);
CREATE INDEX idx_draft_claims_created_by_user_id ON draft_claims (created_by_user_id);
CREATE INDEX idx_draft_claims_status ON draft_claims (status);
