# Backend – Booking Service

## Dokumentation

| Dokument | Pfad |
|----------|------|
| Architektur (arc42) | `docs/arc42/arc42.md` |
| ADR-001 Frontend + Booking Service | `docs/arc42/adrs/ADR-001-frontend-prototyp-und-booking-service.md` |
| ADR-001 Technologie-Stack | `docs/architektur/adrs/ADR-001-technologie-stack-fuer-booking-service.md` |
| ADR-002 Ressourcendaten als Mock | `docs/architektur/adrs/ADR-002-ressourcendaten-als-mock-in-der-spa.md` |
| ADR-003 Basic-Auth ohne Passwörter | `docs/architektur/adrs/ADR-003-authentifizierung-basic-auth-ohne-passwoerter.md` |
| Qualitätsanforderungen | `docs/architektur/qualitätsanforderungen/README.md` |
| Technische Schulden | `docs/architektur/technische-schulden.md` |
| Produkt-Backlog | `docs/produkt/backlog/backlog.md` |
| Glossar (Ubiquitous Language) | `docs/produkt/glossar.md` |

Halte dich immer an das Wording aus dem Glossar.

## Technologie

- **Java 26 Temurin** (via SDKMAN, gepinnt in `.sdkmanrc`)
- **Spring Boot 4.1.0** – kein Spring-Parent-POM, stattdessen Spring Boot BOM in `<dependencyManagement>`
- **Maven Wrapper** (`./mvnw`) – kein globales Maven erforderlich
- **Spring Web MVC** – REST-API
- **Spring Actuator** – Health-Endpoint (einziger aktuell aktiver Endpoint)
- **Spring Boot Testcontainers** – für Integrationstests
- Datenbank: noch nicht eingebunden; geplant ist **H2** im Dateimodus (Prototyp), später **PostgreSQL** (TS-3)
- Authentifizierung: noch nicht eingebunden; geplant ist **Basic-Auth ohne Passwörter** (ADR-003, TS-1)

## Ordner-Struktur

```
backend/
├── .sdkmanrc                          # Java-Version für SDKMAN
├── pom.xml                            # Kein Spring-Parent, Spring BOM via dependencyManagement
├── mvnw / mvnw.cmd                    # Maven Wrapper
└── src/
    ├── main/
    │   ├── java/com/innoq/calvin/booking/
    │   │   ├── BookingServiceApplication.java   # Einstiegspunkt
    │   │   └── <feature>/                       # Feature-basiertes Packaging (z. B. booking/, room/)
    │   └── resources/
    │       └── application.yml                  # YAML-Konfiguration (kein .properties)
    └── test/
        └── java/com/innoq/calvin/booking/
            ├── BookingServiceApplicationTests.java
            ├── TestBookingServiceApplication.java
            └── TestcontainersConfiguration.java
```

## Architektur

**Feature-basiertes Packaging** – Code wird nach Fachdomäne gruppiert, nicht nach technischer Schicht.

```
com.innoq.calvin.booking.booking/    # Buchungsfeature
    BookingController.java           # REST-Endpunkte
    BookingService.java              # Fachlogik
    Booking.java                     # Domänenobjekt / Entity
    BookingRepository.java           # Spring Data Repository
```

Keine tiered Packages wie `controller/`, `service/`, `repository/` auf der obersten Ebene.

**API-Kontext-Pfad:** `/api/v1` (alle Endpunkte und Actuator laufen unter diesem Präfix)  
**Port:** `8081` (Port 8080 ist durch VS Code / code-server belegt)

**Ressourcendaten** (Standorte, Räume, Ausstattung) leben als Mock-Daten im Frontend (ADR-002). Der Booking Service arbeitet ausschließlich mit IDs – er hält und validiert keine Stammdaten.

**Doppelbuchungsprävention (QA-2, höchste Priorität):** Serverseitige Absicherung über JPA-Transaktionen, Optimistic Locking (`@Version`) und Unique-Constraints auf (Raum-ID, Datum, Zeitfenster).

## Wichtige Dateien

| Datei | Zweck |
|-------|-------|
| `pom.xml` | Spring Boot BOM, Abhängigkeiten, `maven.compiler.release=26` |
| `src/main/resources/application.yml` | Port, Kontext-Pfad, Actuator-Konfiguration |
| `src/main/java/.../BookingServiceApplication.java` | `@SpringBootApplication` Einstiegspunkt |
| `.sdkmanrc` | Pinnt `java=26.0.1-tem` für SDKMAN-Auto-Switch |

## Wichtige Bash-Commands

```bash
# Java-Version aktivieren (einmalig pro Shell)
sdk use java 26.0.1-tem

# Build (im backend/-Verzeichnis)
./mvnw clean package -DskipTests

# Starten (Hintergrund, JMX-Stop über Port 9001)
./mvnw spring-boot:start

# Stoppen
./mvnw spring-boot:stop

# Tests ausführen
./mvnw test

# Health-Check
curl http://localhost:8081/api/v1/actuator/health
```

> `spring-boot:start` startet die Anwendung als Hintergrundprozess und registriert einen
> JMX-Stop-Listener auf Port 9001. `spring-boot:stop` beendet diesen Prozess sauber.

## Code Smells – nicht tun

- **Tiered Packaging** (`controller/`, `service/`, `repository/` als Top-Level-Pakete) – stattdessen Feature-Packaging
- **`application.properties`** verwenden – ausschließlich YAML (`application.yml`)
- **Spring-Boot-Parent-POM** (`<parent><artifactId>spring-boot-starter-parent</artifactId></parent>`) – stattdessen BOM via `<dependencyManagement>`
- Geschäftslogik in `@RestController` implementieren – gehört in einen `@Service`
- Stammdaten (Standorte, Räume) im Backend verwalten – sie leben als Mock-Daten in der SPA (ADR-002)
- Neue Actuator-Endpoints exponieren ohne Absprache – aktuell ist nur `health` aktiv

## Run Configurations

### Entwicklung (Vordergrund, mit Live-Reload)

```bash
./mvnw spring-boot:run
```

### Test / CI (Hintergrund)

```bash
./mvnw spring-boot:start   # Starten
./mvnw spring-boot:stop    # Stoppen
```

### Nur Build

```bash
./mvnw clean package -DskipTests
```

## Weitere Hinweise

- **SDKMAN** muss initialisiert sein, damit `sdk` und die gepinnte Java-Version verfügbar sind.
  In einer neuen Shell: `source "$HOME/.sdkman/bin/sdkman-init.sh"` oder der `.sdkmanrc`-Auto-Switch greift,
  wenn `sdkman_auto_use=true` in `~/.sdkman/etc/config` gesetzt ist.
- Der **Stale-Build-Fallstrick**: Wenn `application.properties` und `application.yml` gleichzeitig
  in `target/classes/` existieren, gewinnt `.properties`. Nach Konfigurationsänderungen immer
  `./mvnw clean package` statt nur `package`.
- **Authentifizierung ist noch nicht eingebunden.** Wenn Spring Security hinzugefügt wird,
  Basic-Auth ohne Passwort-Prüfung gemäß ADR-003 implementieren – der Benutzername bestimmt die
  Identität, das Passwort wird ignoriert.
- **H2-Datenbank ist noch nicht eingebunden.** Wenn Persistenz hinzukommt, H2 im Dateimodus
  konfigurieren (Prototyp). Unique-Constraint auf `(raum_id, datum, start_zeit)` für QA-2
  ist zwingend.
