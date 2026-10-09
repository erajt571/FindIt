# FindIt

FindIt is a campus lost-and-found application that helps students report lost and found items, browse recent listings, and receive AI-assisted match suggestions.

## Repository layout

- `apps/frontend`: Next.js web application
- `apps/backend`: Spring Boot REST API
- `docker-compose.yml`: local PostgreSQL database
- `.github/workflows/ci.yml`: continuous integration checks
- `docs/decision-log.md`: approved Phase 0 decisions

## Phase 0 & Phase 1 status

This repo now includes the implementation foundation for the kickoff and foundation phases:

- product and technical decisions captured in `docs/decision-log.md`
- environment templates and secrets guidance
- CI pipeline for build, lint, and test checks
- frontend and backend application skeletons
- PostgreSQL configuration and migration support
- health and API error handling baseline

## Local development

### Prerequisites

- Node.js 18+
- Java 8+
- Docker Desktop (for local Postgres)

### Start the database

```bash
docker compose up -d postgres
```

### Start the backend

```bash
cd apps/backend
./mvnw spring-boot:run
```

### Start the frontend

```bash
cd apps/frontend
npm install
npm run dev
```

## Default local endpoints

- Backend API: http://localhost:8080
- Frontend app: http://localhost:3000
- Postgres: localhost:5432

## Production conventions

The project follows a monorepo structure with backend and frontend separated for easier iteration and deployment.

## License

This project is for internal workshop and prototype use.
