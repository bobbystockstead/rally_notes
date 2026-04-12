# rally_notes

Ktor API for rally notes and related resources.

## Local Environment Setup

This project reads database and server settings from environment variables (see `src/main/resources/application.yaml`).
No secrets should be committed to git.

1. Copy the example env file:

```bash
cp .env.example .env
```

2. Edit `.env` and set your local values.

3. Load env vars in your shell before running the app:

```bash
set -a
source .env
set +a
```

> Ktor does not load `.env` automatically. You must export these variables in your shell or configure them in your IDE run configuration.

## Required Environment Variables

| Variable | Purpose | Default |
|---|---|---|
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `3306` |
| `DB_NAME` | Database name | `rally_notes` |
| `DB_USERNAME` | Database username | `rally_user` |
| `DB_PASSWORD` | Database password | `change_me` |
| `DB_POOL_SIZE` | Hikari pool size | `20` |

## Build and Run

```bash
./gradlew test
./gradlew run
```

## Swagger / OpenAPI

Swagger UI is enabled through `configureOpenAPI()` and exposed at:

- `http://localhost:8080/swagger`

In this project, the OpenAPI spec is generated from route metadata added with Ktor's `describe { ... }` DSL. Route handlers stay in `routes/*Routes.kt`, while OpenAPI definitions are kept in parallel files under `routes/docs/*OpenApi.kt` and attached via helper functions (for example, `attachRallyOpenApi(...)`).

### How to use it

1. Start the API (`./gradlew run`).
2. Open `http://localhost:8080/swagger`.
3. Expand a resource group (for example, `Rallies` or `Teams`).
4. Inspect request/response schemas and status codes.
5. Use **Try it out** to execute requests directly from the browser.

> Note: `GET /health` is intentionally hidden from Swagger to keep API docs focused on business endpoints.
