# Helpdesk + Knowledge Base

Multi-tenant Helpdesk + Knowledge Base SaaS built with Spring Boot 3 and Next.js.

## Highlights
- Tenant isolation with `tenant_id` + Hibernate filters
- JWT auth + RBAC (TENANT_ADMIN, AGENT, VIEWER)
- Outbox events + Kafka publishing + webhook delivery
- Postgres FTS search for tickets and KB
- MinIO attachments
- Redis caching, rate limiting, idempotency keys
- OpenTelemetry tracing + Prometheus metrics

## Repo layout
- `src/` Spring Boot backend
- `frontend/` Next.js dashboard
- `infra/` local observability config
- `docker-compose.yml` full stack

## Local setup

### Prereqs
- Java 17
- Node 20
- Docker

### Run infra + app
```bash
docker-compose up --build
```

Backend: `http://localhost:8080`
Swagger: `http://localhost:8080/swagger-ui.html`
Frontend: `http://localhost:3000`
Prometheus: `http://localhost:9090`
Grafana: `http://localhost:3001`
Jaeger: `http://localhost:16686`

### Backend only
```bash
mvn spring-boot:run
```

### Frontend only
```bash
cd frontend
npm install
npm run dev
```

## Core API flows
- Create tenant + admin: `POST /tenants`
- Login: `POST /auth/login` (set `X-Tenant-Id` header)
- Tickets: `POST /tickets`, `GET /tickets`, `PATCH /tickets/{id}`
- KB: `POST /kb/articles`, `POST /kb/articles/{id}/versions`, `POST /kb/articles/{id}/publish`
- Attachments: `POST /tickets/{id}/attachments`, `GET /attachments/{id}/download`

Headers:
- `Authorization: Bearer <token>`
- `X-Tenant-Id: <tenantSlug>`
- `X-Platform-Admin-Key: <adminKey>` (optional, when enabled)

## Tenant management UI
Set a platform admin key to protect tenant creation:

```
APP_SECURITY_PLATFORM_ADMIN_KEY=your-secret
```

For local dev, create a `.env` file (not committed) based on `.env.example`.

Then use `/dashboard/admin/tenants` to create tenants.

Bootstrap UI (no login required):
`http://localhost:3000/super-admin/tenants`

## Demo credentials
On startup, the backend creates a demo tenant if it does not exist:
- Tenant: `sumit`
- Email: `test@sumit.com`
- Password: generated at startup

The home page fetches these from `GET /public/demo-credentials`.

## Architecture
- Tenant isolation via `TenantFilter` + Hibernate `@Filter`
- JWT auth, method-level RBAC with Spring Security
- Outbox table for reliable event publishing to Kafka
- Webhook delivery with HMAC signing and retries
- Redis-backed cache for KB reads and rate limiting

## Tests
```bash
mvn test
cd frontend
npm run test:e2e
```
