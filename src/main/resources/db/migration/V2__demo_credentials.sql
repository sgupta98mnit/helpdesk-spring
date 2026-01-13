CREATE TABLE demo_credentials (
    id UUID PRIMARY KEY,
    tenant_slug VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(200) NOT NULL,
    password VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
