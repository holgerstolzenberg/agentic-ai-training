# Backend – Booking Service

## Documentation

| Document | Path |
|----------|------|
| Architecture (arc42) | `docs/arc42/arc42.md` |
| ADR-001 Frontend + Booking Service | `docs/arc42/adrs/ADR-001-frontend-prototype-and-booking-service.md` |
| ADR-001 Technology Stack | `docs/architecture/adrs/ADR-001-technology-stack-for-booking-service.md` |
| ADR-002 Resource Data as Mock | `docs/architecture/adrs/ADR-002-resource-data-as-mock-in-spa.md` |
| ADR-003 Basic Auth without Passwords | `docs/architecture/adrs/ADR-003-authentication-basic-auth-without-passwords.md` |
| Quality Requirements | `docs/architecture/quality-requirements/README.md` |
| Technical Debt | `docs/architecture/technical-debt.md` |
| Product Backlog | `docs/product/backlog/backlog.md` |
| Glossary (Ubiquitous Language) | `docs/product/glossary.md` |

Always follow the wording from the glossary.

## Technology

- **Java 26 Temurin** (via SDKMAN, pinned in `.sdkmanrc`)
- **Spring Boot 4.1.0** – no Spring parent POM; instead Spring Boot BOM in `<dependencyManagement>`
- **Maven Wrapper** (`./mvnw`) – no global Maven required
- **Spring Web MVC** – REST API
- **Spring Actuator** – health endpoint (only currently active endpoint)
- **Spring Boot Testcontainers** – for integration tests
- Database: not yet integrated; planned is **H2** in file mode (prototype), later **PostgreSQL** (TS-3)
- Authentication: not yet integrated; planned is **Basic Auth without passwords** (ADR-003, TS-1)

## Folder Structure

```
backend/
├── .sdkmanrc                          # Java version for SDKMAN
├── pom.xml                            # No Spring parent, Spring BOM via dependencyManagement
├── mvnw / mvnw.cmd                    # Maven Wrapper
└── src/
    ├── main/
    │   ├── java/com/innoq/calvin/booking/
    │   │   ├── BookingServiceApplication.java   # Entry point
    │   │   └── <feature>/                       # Feature-based packaging (e.g. booking/, room/)
    │   └── resources/
    │       └── application.yml                  # YAML configuration (no .properties)
    └── test/
        └── java/com/innoq/calvin/booking/
            ├── BookingServiceApplicationTests.java
            ├── TestBookingServiceApplication.java
            └── TestcontainersConfiguration.java
```

## Architecture

**Feature-based packaging** – code is grouped by business domain, not by technical layer.

```
com.innoq.calvin.booking.booking/    # Booking feature
    BookingController.java           # REST endpoints
    BookingService.java              # Business logic
    Booking.java                     # Domain object / entity
    BookingRepository.java           # Spring Data repository
```

No tiered packages like `controller/`, `service/`, `repository/` at the top level.

**API context path:** `/api/v1` (all endpoints and Actuator run under this prefix)  
**Port:** `8081` (port 8080 is occupied by VS Code / code-server)

**Resource data** (locations, rooms, equipment) live as mock data in the frontend (ADR-002). The Booking Service works exclusively with IDs — it does not hold or validate master data.

**Double-booking prevention (QA-2, highest priority):** Server-side protection via JPA transactions, Optimistic Locking (`@Version`), and unique constraints on (room ID, date, time slot).

## Important Files

| File | Purpose |
|-------|-------|
| `pom.xml` | Spring Boot BOM, dependencies, `maven.compiler.release=26` |
| `src/main/resources/application.yml` | Port, context path, Actuator configuration |
| `src/main/java/.../BookingServiceApplication.java` | `@SpringBootApplication` entry point |
| `.sdkmanrc` | Pins `java=26.0.1-tem` for SDKMAN auto-switch |

## Important Bash Commands

```bash
# Activate Java version (once per shell)
sdk use java 26.0.1-tem

# Build (in the backend/ directory)
./mvnw clean package -DskipTests

# Start (background, JMX stop via port 9001)
./mvnw spring-boot:start

# Stop
./mvnw spring-boot:stop

# Run tests
./mvnw test

# Health check
curl http://localhost:8081/api/v1/actuator/health
```

> `spring-boot:start` starts the application as a background process and registers a
> JMX stop listener on port 9001. `spring-boot:stop` gracefully terminates this process.

## Code Smells – Don't Do

- **Tiered packaging** (`controller/`, `service/`, `repository/` as top-level packages) – use feature packaging instead
- **`application.properties`** – use exclusively YAML (`application.yml`)
- **Spring Boot parent POM** (`<parent><artifactId>spring-boot-starter-parent</artifactId></parent>`) – use BOM via `<dependencyManagement>` instead
- Implement business logic in `@RestController` – it belongs in a `@Service`
- Manage master data (locations, rooms) in the backend – they live as mock data in the SPA (ADR-002)
- Expose new Actuator endpoints without agreement – currently only `health` is active

## Run Configurations

### Development (foreground, with live reload)

```bash
./mvnw spring-boot:run
```

### Test / CI (background)

```bash
./mvnw spring-boot:start   # Start
./mvnw spring-boot:stop    # Stop
```

### Build only

```bash
./mvnw clean package -DskipTests
```

## Additional Notes

- **SDKMAN** must be initialized for `sdk` and the pinned Java version to be available.
  In a new shell: `source "$HOME/.sdkman/bin/sdkman-init.sh"` or the `.sdkmanrc` auto-switch
  takes effect when `sdkman_auto_use=true` is set in `~/.sdkman/etc/config`.
- The **stale build pitfall**: if `application.properties` and `application.yml` both exist in
  `target/classes/`, `.properties` wins. After configuration changes always run
  `./mvnw clean package` instead of just `package`.
- **Authentication is not yet integrated.** When Spring Security is added,
  implement Basic Auth without password verification per ADR-003 — the username determines
  the identity, the password is ignored.
- **H2 database is not yet integrated.** When persistence is added, configure H2 in file mode
  (prototype). A unique constraint on `(raum_id, datum, start_zeit)` for QA-2 is mandatory.
