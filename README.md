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

## VPS Deployment

This project is configured for deployment on a Virtual Private Server (VPS) using Docker and Docker Compose.

### Prerequisites on your VPS

*   **Docker and Docker Compose:** Install Docker and Docker Compose.
*   **Nginx:** Install Nginx to act as a reverse proxy.

### Deployment Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sgupta98mnit/helpdesk-spring.git
    cd helpdesk-spring
    ```

2.  **Configure Environment Variables:**
    *   A `.env` file has been created for you. You **must** update the placeholder values in this file for production.
    *   **`APP_SECURITY_JWT_SECRET`**: A strong, unique secret for signing JWTs. You can generate one with `openssl rand -hex 32`.
    *   **`APP_SECURITY_PLATFORM_ADMIN_KEY`**: A secret key to protect the tenant creation endpoint.
    *   **`NEXT_PUBLIC_API_BASE_URL`**: The full public URL to your backend API. This has been pre-configured to `http://sumit-gupta.cloud/projects/helpdesk-spring/api` but you should verify it matches your setup.

3.  **Configure the Reverse Proxy (Nginx):**
    *   A sample Nginx configuration is provided in `nginx.conf.example`.
    *   Copy or link this configuration to your Nginx configuration directory (e.g., `/etc/nginx/sites-available/`).
    *   Make sure to update `server_name` to your domain (`sumit-gupta.cloud`).
    *   Enable the site and restart Nginx.

    Example for Ubuntu:
    ```bash
    sudo cp nginx.conf.example /etc/nginx/sites-available/helpdesk
    sudo ln -s /etc/nginx/sites-available/helpdesk /etc/nginx/sites-enabled/
    sudo nginx -t # Test configuration
    sudo systemctl restart nginx
    ```

4.  **Build and Run the Application:**
    *   Use Docker Compose to build and run all the services in detached mode:
    ```bash
    docker-compose up --build -d
    ```

5.  **Accessing your Application:**
    *   **Frontend:** `http://sumit-gupta.cloud/projects/helpdesk-spring`
    *   **Backend API:** `http://sumit-gupta.cloud/projects/helpdesk-spring/api`

### Port Configuration

*   The backend service is configured to run on port `8081` to avoid conflicts.
*   The frontend service is configured to run on port `3001`.
*   Nginx listens on port `80` and proxies requests to the appropriate services based on the URL path.
