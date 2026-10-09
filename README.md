# FindIt

FindIt is a campus lost-and-found application. Users can report lost and found items, browse and filter reports, receive possible matches, and coordinate recovery. Administrators can moderate reports and suspend accounts.

## Repository layout

- `apps/frontend`: Next.js and TypeScript web application
- `apps/backend`: Spring Boot REST API
- `apps/backend/src/main/resources/db/migration`: Flyway database migrations
- `docker-compose.yml`: local PostgreSQL, API, and frontend services
- `render.yaml`: optional Render demo blueprint
- `docs/decision-log.md`: product and architecture decisions

## Run locally with PostgreSQL

Requirements:

- Node.js 18+
- Java 8
- PostgreSQL 16+ (or Docker Compose)

### Option A: Use an installed PostgreSQL server

Create a local database and application role using your PostgreSQL administrator account:

```sql
CREATE ROLE findit LOGIN PASSWORD 'choose-a-local-password';
CREATE DATABASE findit OWNER findit;
```

Set the API environment in PowerShell, using the same password:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'postgres,demo'
$env:APP_ENV = 'local'
$env:DB_HOST = 'localhost'
$env:DB_PORT = '5432'
$env:DB_NAME = 'findit'
$env:DB_USERNAME = 'findit'
$env:DB_PASSWORD = 'choose-a-local-password'
$env:DEMO_MODE = 'true'
$env:DEMO_ADMIN_EMAIL = 'admin@findit.local'
$env:DEMO_ADMIN_PASSWORD = 'set-a-unique-password-of-at-least-12-characters'
$env:DEMO_USER_EMAIL = 'student@findit.local'
$env:DEMO_USER_PASSWORD = 'set-another-unique-password-of-at-least-12-characters'
```

Start the API in one terminal:

```powershell
cd apps\backend
.\mvnw.cmd spring-boot:run
```

Flyway applies the schema migrations on startup. The demo profile creates the configured local admin and student accounts and sample reports; it refuses short passwords and must not be enabled in production.

Start the frontend in another terminal:

```powershell
cd apps\frontend
npm install
$env:FINDIT_BACKEND_URL = 'http://localhost:8080'
npm.cmd run dev
```

The frontend is at <http://localhost:3000>, the API is at <http://localhost:8080>, and the health endpoint is <http://localhost:8080/api/health>. Sign in with the demo emails and the passwords you configured above.

### Option B: Use Docker Compose

Install Docker Desktop (including Docker Compose), copy `.env.example` to `.env`, and change the local database password. From the repository root, start the stack:

```powershell
Copy-Item .env.example .env
# Edit .env and set POSTGRES_PASSWORD to a local password.
docker compose up --build
```

Compose starts PostgreSQL, the API, and the frontend. Open <http://localhost:3000> in a browser; the API health endpoint is <http://localhost:8080/api/health>. No demo accounts are seeded by default; register an account in the app. For local use, the application database password is read from `.env`; do not use the example password outside a local environment.

## Development and validation

Backend tests use an in-memory H2 database in PostgreSQL compatibility mode:

```powershell
cd apps\backend
.\mvnw.cmd clean test
```

Frontend checks:

```powershell
cd apps\frontend
npm.cmd ci
npm.cmd run lint
npm.cmd run build
npx.cmd playwright install chromium
npm.cmd run test:e2e
```

The browser suite starts the backend with its H2 test profile and the Next.js development server automatically. It covers account registration and sign-in, report creation and search persistence, authentication failures, and public report privacy. CI installs Chromium with its required system dependencies before running the suite.

The PostgreSQL profile uses the real PostgreSQL driver and Flyway migrations. To validate changes against PostgreSQL, run the API with `SPRING_PROFILES_ACTIVE=postgres` and the database environment variables above.

## Current scope and limitations

- Matching runs asynchronously through persisted jobs, with deterministic local text similarity, weighted scoring, deduplication, and bounded retry. A real external embedding provider is not configured; no provider credentials are required for local development.
- Report image URLs can be stored, but an image-upload pipeline and managed object-storage integration are not implemented.
- `render.yaml` describes a demo deployment, but the app is not hosted by this repository. Deploying it requires a Render account and project credentials; no hosted URL is currently available.
- The demo seeder is intended only for local or demo profiles. Do not enable demo accounts in production.

## Deployment and recovery

The Render blueprint provisions the API, frontend, and managed PostgreSQL database. Configure environment values and generated demo passwords in the hosting dashboard; never commit production secrets. Flyway migrations are append-only: apply new migrations forward, and use a verified database backup/restore for data recovery rather than editing an already-applied migration.

To reset a local demo, stop the application, back up any data you need, and recreate only the dedicated `findit` development database. On restart, the demo profile seeds the accounts and sample reports when the tables are empty.

## License

This project is for internal workshop and prototype use.
