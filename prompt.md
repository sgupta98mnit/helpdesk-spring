````markdown
# task.md — Multi-tenant Helpdesk + Knowledge Base (Spring Boot + Next.js)

## Goal
Build a resume-ready, multi-tenant SaaS: “Helpdesk + Knowledge Base” with a Spring Boot backend and a Next.js dashboard.

## Project rules
- Build the entire application in one go (do not split into sequential tasks).
- Keep `README.md` up to date with setup, architecture, and how to run the stack.

## Non-negotiable requirements
- Tenant isolation: all tenant-scoped tables include `tenant_id` and all queries are tenant-restricted.
- Auth: JWT access + refresh tokens, RBAC (`TENANT_ADMIN`, `AGENT`, `VIEWER`).
- Event-driven: Outbox pattern + Kafka for domain events.
- Production-like: Flyway migrations, tests, observability, CI, docker-compose.
- Next.js dashboard: clean UI with tables, filters, detail and admin pages.

## Tech stack
- Backend: Java 17, Spring Boot 3, Spring Security, JPA, Flyway, MapStruct, OpenAPI, Testcontainers
- Infra: Postgres, Redis, Kafka, MinIO, OpenTelemetry, Prometheus, Grafana, Jaeger/Tempo
- Frontend: Next.js App Router, TypeScript, Tailwind, shadcn/ui, TanStack Query, Zod, Playwright

## Repo layout (recommended)
- `/backend` — Spring Boot app
- `/frontend` — Next.js dashboard
- `/infra` (optional) — scripts, dashboards, diagrams
- `docker-compose.yml` at root (full stack)

# BACKEND TASKS (run one at a time)

## B1 — Backend init + core dependencies

**Goal**: Start a clean Spring Boot 3 project that boots and exposes health + swagger.

```text
Task B1:
- Create Spring Boot 3 (Java 17) project (Gradle or Maven).
- Dependencies: Web, Security, Data JPA, Validation, Actuator, Flyway, Lombok, MapStruct, OpenAPI, Testcontainers.
- Add formatting/lint (Spotless or Checkstyle).
- Add basic package structure: config, auth, tenant, tickets, kb, common, etc.
- Add /actuator/health and Swagger UI enabled.
Done when:
- App starts locally.
- /actuator/health returns UP.
- Swagger UI loads.
```

## B2 — docker-compose baseline (infra)

```text
Task B2:
- Create root docker-compose.yml that runs:
  postgres, redis, kafka, minio (+console)
- Add sane default env vars and ports.
- Add README “Local Infra” section.
Done when:
- docker-compose up starts all infra containers successfully.
```

## B3 — Multi-tenancy foundation (tenant_id isolation)

```text
Task B3:
- Add tenant table + model.
- Implement TenantContext resolved from X-Tenant-Id header.
- Enforce tenant isolation via Hibernate filter or equivalent.
- Add integration test using Testcontainers Postgres proving:
  tenant A cannot read tenant B data.
Done when:
- TenantContext works.
- Test passes showing isolation.
```

## B4 — Auth + RBAC (JWT + refresh)

```text
Task B4:
- Entities: User (tenant-scoped), Role enum (TENANT_ADMIN, AGENT, VIEWER).
- Implement endpoints:
  POST /auth/login
  POST /auth/refresh
  POST /auth/logout
  GET /me
- Secure with RBAC.
- Add tests for auth + role enforcement.
Done when:
- Login returns tokens.
- /me works with JWT.
- Unauthorized/forbidden cases verified by tests.
```

## B5 — Tenant provisioning API (create tenant + first admin)

```text
Task B5:
- POST /tenants creates tenant + first admin user.
- Validate unique tenant slug.
- Add Flyway migrations.
Done when:
- Can create tenant then login as admin under that tenant.
```

## B6 — Tickets CRUD + comments (tenant-scoped)

```text
Task B6:
- Entities: Ticket, TicketComment.
- Ticket fields: title, description, status, priority, requesterEmail, assigneeUserId, timestamps, tenant_id.
- APIs:
  POST /tickets
  GET /tickets?status=&q=&page=
  GET /tickets/{id}
  PATCH /tickets/{id} (status/assignee/priority)
  POST /tickets/{id}/comments
- RBAC: VIEWER read-only; AGENT/ADMIN can write.
Done when:
- Full flow works in Swagger.
- Tenant isolation enforced.
```

## B7 — Knowledge Base (articles + versioning + publish)

```text
Task B7:
- Entities: Article, ArticleVersion.
- APIs:
  POST /kb/articles
  POST /kb/articles/{id}/versions
  POST /kb/articles/{id}/publish
  GET /kb/articles?status=&q=
  GET /kb/articles/{slug}
- Draft only visible to AGENT/ADMIN; published visible to all roles.
Done when:
- Versioning + publish flow works.
```

## B8 — Audit logs (ticket + KB)

```text
Task B8:
- Add AuditLog entity: actorUserId, action, resourceType, resourceId, beforeJson, afterJson, timestamp, tenant_id.
- Log ticket create/update/comment and article publish.
- Endpoint: GET /audit?resourceType=&resourceId=&page=
Done when:
- Actions produce audit entries.
- Endpoint returns logs.
```

## B9 — Full-text search (Postgres FTS)

```text
Task B9:
- Implement Postgres FTS search for:
  tickets (title + description)
  KB (published articles title + content)
- Endpoints:
  GET /search/tickets?q=
  GET /search/kb?q=
Done when:
- Queries return relevant results and are tenant-safe.
```

## B10 — Attachments with MinIO (S3)

```text
Task B10:
- Add Attachment entity and MinIO integration.
- APIs:
  POST /tickets/{id}/attachments (multipart)
  GET /attachments/{id}/download
- Security: only same-tenant users can download.
Done when:
- Upload and download works via Swagger.
```

## B11 — Events + Outbox + Kafka

```text
Task B11:
- Add Outbox table: id, tenant_id, eventType, payloadJson, createdAt, publishedAt, status.
- On ticket and KB events, write outbox record in same transaction.
- Background publisher publishes to Kafka.
- Add a consumer inside app that logs events (for now).
Done when:
- Events published reliably.
- Testcontainers Kafka test exists.
```

## B12 — Notifications (in-app; optional email later)

```text
Task B12:
- Notification entity: tenant_id, user_id, type, message, read=false, createdAt.
- Create notifications on ticket assignment/status changes.
- Endpoints:
  GET /notifications
  POST /notifications/{id}/read
Done when:
- Notifications created and readable.
```

## B13 — Webhooks + signed delivery + retries

```text
Task B13:
- Tenant can register webhooks: url, events, secret, enabled.
- Deliver webhooks on events, sign with HMAC.
- Retries with exponential backoff; dead-letter after max attempts.
- Endpoints:
  POST /webhooks
  GET /webhooks
  DELETE /webhooks/{id}
Done when:
- Webhook delivery works and retries on failures.
```

## B14 — Redis caching + rate limiting + idempotency keys

```text
Task B14:
- Cache published KB reads in Redis (invalidate on publish).
- Add rate limiting per user or API key.
- Add Idempotency-Key support for POST /tickets to avoid duplicates.
Done when:
- Cache works (visible reduction in DB calls).
- Rate limit enforced.
- Idempotency verified.
```

## B15 — Observability + CI/CD

```text
Task B15:
- Add OpenTelemetry tracing (Jaeger/Tempo in compose).
- Add Prometheus metrics + Grafana dashboard.
- Add structured JSON logs + correlation IDs.
- Add GitHub Actions: build, tests (including Testcontainers), format, docker build.
Done when:
- Traces/metrics visible locally.
- CI passes on PR.
```

---

# TASKS — FRONTEND (run one at a time)

## F1 — Next.js init + dashboard shell

```text
Task F1:
- Create Next.js App Router + TypeScript app.
- Add Tailwind + shadcn/ui.
- Create layout shell: sidebar + topbar + responsive.
- Routes:
  /login
  /dashboard
Done when:
- App runs, layout visible on /dashboard.
```

## F2 — API client + TanStack Query provider

```text
Task F2:
- Implement typed fetch wrapper with base URL.
- Add TanStack Query provider and standard config.
- Prepare tenant header injection (X-Tenant-Id).
Done when:
- One sample request works (can be mocked).
```

## F3 — Auth flow (JWT + refresh handling)

```text
Task F3:
- Login form using Zod validation.
- Store access token safely; refresh token via httpOnly cookie if backend supports.
- Implement auto-refresh on 401 and retry request.
- Protected routes redirect to /login.
Done when:
- Login works and /dashboard is protected.
```

## F4 — Tenant selector + persistence

```text
Task F4:
- Add tenant selector in topbar.
- Persist selected tenant (cookie/localStorage).
- Ensure all requests send X-Tenant-Id.
Done when:
- Switching tenant changes data calls.
```

## F5 — Tickets list (table, filters, pagination)

```text
Task F5:
- /dashboard/tickets
- Table columns: id, title, status, priority, assignee, updatedAt
- Filters: status dropdown, search q
- Pagination controls
Done when:
- List works with loading/empty/error states.
```

## F6 — Ticket detail + comments + RBAC gating

```text
Task F6:
- /dashboard/tickets/[id]
- Show ticket details and update controls (status/assignee/priority).
- Comments list + add comment.
- RBAC: VIEWER cannot write.
Done when:
- Ticket detail and comment flow works.
```

## F7 — Create ticket (modal or page) + Idempotency-Key

```text
Task F7:
- Create Ticket form with Zod validation.
- POST /tickets with Idempotency-Key (UUID per submission).
- Navigate to detail on success.
Done when:
- Ticket creation works and prevents duplicates on retries.
```

## F8 — Attachments UI (upload + download)

```text
Task F8:
- Upload attachments from ticket detail (multipart).
- Show list of attachments with download action.
- Show progress + errors.
Done when:
- Attachments work end-to-end.
```

## F9 — KB list + search

```text
Task F9:
- /dashboard/kb
- Search input + status filter
- List view with navigation to article page
Done when:
- KB browsing works.
```

## F10 — KB article view (markdown render + access rules)

```text
Task F10:
- /dashboard/kb/[slug]
- Render markdown content cleanly.
- Enforce visibility: published for all; draft for agent/admin.
Done when:
- KB detail renders correctly and respects access.
```

## F11 — KB editor + versioning + publish

```text
Task F11:
- /dashboard/kb/edit/[id]
- Editor UI (textarea acceptable; nicer editor optional).
- Create version + publish actions.
- RBAC: AGENT/ADMIN only.
Done when:
- Draft/version/publish flow works.
```

## F12 — Notifications inbox

```text
Task F12:
- /dashboard/notifications
- List notifications; mark as read.
- Unread badge in sidebar.
Done when:
- Notifications flow works.
```

## F13 — Admin: Users/Invites (TENANT_ADMIN only)

```text
Task F13:
- /dashboard/admin/users
- List users + roles
- Invite user flow (email + role)
- RBAC gating in UI
Done when:
- Admin screen works and is protected.
```

## F14 — Admin: Webhooks management

```text
Task F14:
- /dashboard/admin/webhooks
- CRUD webhooks + event subscriptions
- Show status fields if available
Done when:
- Webhooks UI is functional and clean.
```

## F15 — Admin: Audit log viewer

```text
Task F15:
- /dashboard/admin/audit
- Table + filters (resourceType, resourceId, actor)
- Basic before/after diff view
Done when:
- Audit log UI works and looks professional.
```

## F16 — Realtime updates (optional high-value)

```text
Task F16:
- Add SSE/WebSocket client for ticket events.
- Auto-refresh ticket list/detail when events arrive.
Done when:
- Realtime updates visible without manual refresh.
```

## F17 — Frontend hardening (toasts, error boundaries)

```text
Task F17:
- Add toast system for success/error.
- Add error boundary pages and consistent empty states.
Done when:
- UX is production-like across pages.
```

## F18 — Dockerize frontend + full-stack compose

```text
Task F18:
- Add Dockerfile for Next.js.
- Update docker-compose to run frontend + backend.
- Add env vars for API base URL.
Done when:
- One command starts full stack locally.
```

## F19 — Playwright E2E + CI integration

```text
Task F19:
- Add Playwright tests:
  login
  create ticket
  add comment
- Add GitHub Actions step to run e2e tests.
Done when:
- CI runs e2e headless successfully.
```

---

## API Contracts (must stay consistent)

Headers:

* `Authorization: Bearer <accessToken>`
* `X-Tenant-Id: <tenantId or tenantSlug>`

Core endpoints used by dashboard:

* Auth: `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`, `GET /me`
* Tenants: `POST /tenants`
* Tickets: `POST /tickets`, `GET /tickets`, `GET /tickets/{id}`, `PATCH /tickets/{id}`, `POST /tickets/{id}/comments`
* Attachments: `POST /tickets/{id}/attachments`, `GET /attachments/{id}/download`
* KB: `POST /kb/articles`, `POST /kb/articles/{id}/versions`, `POST /kb/articles/{id}/publish`, `GET /kb/articles`, `GET /kb/articles/{slug}`
* Search: `GET /search/tickets`, `GET /search/kb`
* Notifications: `GET /notifications`, `POST /notifications/{id}/read`
* Audit: `GET /audit`
* Webhooks: `POST /webhooks`, `GET /webhooks`, `DELETE /webhooks/{id}`

---

## Final Deliverables Checklist (resume-ready)

* Running full stack via docker-compose
* Swagger docs + clean API
* Tenant isolation proven with tests
* Kafka outbox events + consumer
* Redis caching + rate limiting + idempotency
* MinIO file uploads
* Full Next.js dashboard with admin pages
* Observability stack (Prometheus/Grafana + tracing)
* CI/CD pipeline with integration tests + Playwright

---

## Suggested Resume Bullet (use later)

* Built a multi-tenant Helpdesk + Knowledge Base SaaS using Spring Boot 3 and Next.js, implementing JWT auth, RBAC, tenant isolation, Kafka outbox events, Redis caching, Postgres FTS search, MinIO attachment storage, OpenTelemetry tracing, Prometheus/Grafana monitoring, and CI/CD with GitHub Actions and Testcontainers.
````
