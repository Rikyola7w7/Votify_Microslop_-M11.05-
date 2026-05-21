# Votify — Agent Guide

## Dev Server
```
./mvnw spring-boot:run
```
Browser auto-opens. Uses `application.properties` with PostgreSQL (Neon).

## Testing
```
./mvnw test
```
Test profile auto-activates `application-test.properties` — H2 in-memory, `create-drop` schema.

## Production Build
```
./mvnw package -Pproduction
```

## Key Config
- **Java 21**, **Spring Boot 4.0.3**, **Vaadin 25.0.6**
- **PostgreSQL** (Neon) in prod; **H2** in tests
- **Flyway** migrations in `src/main/resources/db/migration/` (V1–V8)
- CSRF disabled, all routes `permitAll` — no auth enforced yet
- `SeedRunner` resets PostgreSQL sequences on every startup
- `neon.session.sql` (root) is empty — seed data not yet populated

## Architecture
- **Vaadin server-side UI** — no REST controllers; views call services directly
- Entities use **`jakarta.persistence`** (not `javax.persistence`)
- All repositories extend `JpaRepository` and `JpaSpecificationExecutor`
- `com.microslop` base package

## Adding New Entities
Follow the layer order: `entity` → `repository` → `service` → `views`

## Conventions
- Use **Lombok** for entities (configured in maven-compiler-plugin)
- Constructor-based dependency injection for all services
- Test classes mirror main package structure under `src/test/java/`