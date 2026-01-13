CREATE TABLE tenants (
    id UUID PRIMARY KEY,
    slug VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX idx_users_tenant_email ON users(tenant_id, email);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    token VARCHAR(200) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_refresh_tokens_tenant_token ON refresh_tokens(tenant_id, token);

CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    requester_email VARCHAR(200) NOT NULL,
    assignee_user_id VARCHAR(64),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_tickets_tenant_status ON tickets(tenant_id, status);
CREATE INDEX idx_tickets_fts ON tickets USING GIN (to_tsvector('english', title || ' ' || description));

CREATE TABLE ticket_comments (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    ticket_id VARCHAR(64) NOT NULL,
    author_user_id VARCHAR(64) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_ticket_comments_ticket ON ticket_comments(ticket_id);

CREATE TABLE kb_articles (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    current_version INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_kb_articles_tenant_status ON kb_articles(tenant_id, status);
CREATE INDEX idx_kb_articles_fts ON kb_articles USING GIN (to_tsvector('english', title));

CREATE TABLE kb_article_versions (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    article_id VARCHAR(64) NOT NULL,
    version_number INTEGER NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_kb_versions_article ON kb_article_versions(article_id, version_number);
CREATE INDEX idx_kb_versions_fts ON kb_article_versions USING GIN (to_tsvector('english', content));

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    actor_user_id VARCHAR(64) NOT NULL,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(64) NOT NULL,
    before_json TEXT,
    after_json TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE attachments (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    ticket_id VARCHAR(64) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    s3key VARCHAR(500) NOT NULL,
    size_bytes BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(200) NOT NULL,
    payload_json TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_outbox_status ON outbox_events(status, created_at);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    type VARCHAR(100) NOT NULL,
    message VARCHAR(255) NOT NULL,
    read BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE webhooks (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    url VARCHAR(500) NOT NULL,
    events VARCHAR(500) NOT NULL,
    secret VARCHAR(200) NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE webhook_deliveries (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    webhook_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(200) NOT NULL,
    payload_json TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    attempt_count INTEGER NOT NULL,
    next_attempt_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE idempotency_keys (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    key_value VARCHAR(200) NOT NULL,
    resource_id VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_idempotency_tenant_key ON idempotency_keys(tenant_id, key_value);
