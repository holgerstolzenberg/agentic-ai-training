# ADR-001: Technologie-Stack für den Booking Service

**Status:** Akzeptiert
**Datum:** 2026-06-17
**Entscheider:** Entwicklungsteam gemeinsam mit KI-Agent (abgewogene Empfehlung)

## Kontext und Problemstellung

Calvin besteht aus einem bestehenden Angular-SPA-Frontend und einem noch zu
implementierenden **Booking Service** als Backend. Die grundsätzliche Aufteilung
(SPA + separater Booking Service, Kommunikation über REST/JSON über HTTPS,
Buchungslogik serverseitig) ist bereits entschieden – siehe
[ADR-001 (arc42): Frontend-Prototyp mit separatem Booking Service](../../arc42/adrs/ADR-001-frontend-prototyp-und-booking-service.md).

Offen ist die Wahl des **Technologie-Stacks** für diesen Service. Der Service wird
in einer späteren Aufgabe **durch einen KI-Agenten implementiert**; daraus folgt
eine ungewöhnlich hohe Gewichtung der LLM-Implementierbarkeit (Mainstream-Stack,
stabile Konventionen, große Referenzbasis).

## Entscheidungskriterien

1. **REST-API** für das Angular-Frontend.
2. **Kompatibilität mit dateibasierter Datenbank** (z. B. SQLite, H2) – kein
   separater DB-Server im Prototyp.
3. **Perspektivische Okta-Integration** (OIDC/OAuth2) zur Absicherung der API.
4. **Schnelle Entwicklung** möglich.
5. **Technologie, in der die Implementierung sicher beherrscht wird** (mainstream,
   viel Referenzmaterial → zuverlässige Generierung durch den KI-Agenten).

Zusätzlich relevant aus den [Qualitätsanforderungen](../qualitätsanforderungen/README.md):
QA-2 (Doppelbuchungsprävention ≥ 99,9 %, höchste Priorität) verlangt zuverlässige
serverseitige Transaktions-/Concurrency-Mechanismen; QA-1 verlangt schnelle
Lese-Antwortzeiten (≤ 500 ms).

## Betrachtete Optionen

### Option A – Spring Boot (Java)

Spring Web + Spring Data JPA + Spring Security, H2 als eingebettete Datenbank.

| Kriterium | Pro | Contra |
|-----------|-----|--------|
| REST | `@RestController` ist De-facto-Standard; JSON (Jackson) out-of-the-box; OpenAPI via springdoc trivial | Mehr Boilerplate (DTOs/Mapping) |
| Embedded DB | **H2 ist der native Java-Embedded-Store**, Spring-Boot-Auto-Config; späterer Wechsel H2 → PostgreSQL nur Config + Dependency | H2-Dateimodus ist Prototyp-, nicht Produktionslösung |
| Okta/OIDC | **Reifster Kandidat:** Spring Security OAuth2 Resource Server, offizielle Okta-Guides, JWT-Validierung deklarativ über `issuer-uri` | Spring Security hat (für Menschen) steile Lernkurve |
| Dev-Speed | Spring Initializr, Auto-Config, Spring Data Repositories (CRUD ohne SQL) | Höchste Build-/Startup-Latenz, verbosester Code |
| LLM-Implementierbarkeit | **Sehr stark:** seit Jahren stabile Konventionen, überwältigend in Trainingsdaten | Versions-Drift Spring Boot 2↔3 (Jakarta) – durch Versionsfixierung beherrschbar |
| QA-2 | JPA-Transaktionen + Optimistic Locking (`@Version`) + Unique-Constraints sind kanonische Standard-Patterns | – |

### Option B – NestJS (Node.js/TypeScript)

SQLite via Prisma/TypeORM, Passport/`openid-client` für Okta.

| Kriterium | Pro | Contra |
|-----------|-----|--------|
| REST | Sauberes Controller-Modell; **gleiche Sprache wie das Frontend** (geteilte Typen/Mentalmodell) | – |
| Embedded DB | Prisma + SQLite erstklassig dokumentiert | Zwei konkurrierende ORMs (Prisma vs. TypeORM) – erhöht Risiko inkohärenten Codes |
| Okta/OIDC | Passport + `openid-client` funktionieren | **Schwächster, fragmentiertester Auth-Pfad:** viele Strategien, Breaking Changes in `openid-client`, weniger kanonische Okta-Guides |
| Dev-Speed | Schneller Reload, gute DX | Decorator/`reflect-metadata`-Setup-Stolpersteine |
| LLM-Implementierbarkeit | Mainstream, gut vertreten | Hohe Ökosystem-Volatilität (ORM-Wahl, ESM/CJS, Passport-Versionen) |
| QA-2 | Transaktionen via Prisma möglich | SQLite single-writer; Nebenläufigkeits-Pattern im JS-Umfeld weniger kanonisch dokumentiert |

### Option C – FastAPI (Python)

SQLite via SQLAlchemy, OIDC-Libs für Okta.

| Kriterium | Pro | Contra |
|-----------|-----|--------|
| REST | Sehr knapper Code; **automatische OpenAPI/Swagger-Docs** ab Werk; Pydantic-Validierung exzellent | – |
| Embedded DB | SQLite ist Pythons „Batterie-Datenbank" (stdlib); SQLAlchemy + SQLite sehr gut dokumentiert | SQLAlchemy 1.x↔2.0 Syntaxbruch – durch Versionspinning beherrschbar |
| Okta/OIDC | OIDC-JWT-Validierung via Security-Dependencies + `authlib`/`python-jose`; gute Beispiele | Kein „Batteries-included"-Security-Framework wie Spring Security; mehr Handarbeit |
| Dev-Speed | **Schnellste Iteration** der drei, minimaler Boilerplate | – |
| LLM-Implementierbarkeit | **Sehr stark**, idiomatisch generierbar | – |
| QA-2 | Transaktion + Unique-Constraint auf (Raum, Zeitfenster) gut umsetzbar | SQLite single-writer wie Option B |

## Entscheidung

Wir wählen **Option A – Spring Boot (Java)** mit folgendem konkreten Stack:

- **Java 21 (LTS)**, **Spring Boot 3.x**, Build mit **Maven**
- **Spring Web** für die REST-API (JSON), API-Dokumentation via **springdoc-openapi**
- **Spring Data JPA** als Persistenzschicht
- **H2** im **Dateimodus** als eingebettete Datenbank (Prototyp); JPA hält den
  späteren Wechsel auf **PostgreSQL** gering
- **Spring Security – OAuth2 Resource Server** als vorbereitete **Okta**-Integration
  (JWT-Validierung über `issuer-uri`; im Prototyp deaktivier-/zuschaltbar)

## Begründung

Da das Backend von einem KI-Agenten gebaut wird und QA-2 (Doppelbuchungsprävention)
die höchstpriorisierte Anforderung ist, dominieren zwei Kriterien: **maximale
LLM-Implementierbarkeit** und **reife, kanonisch dokumentierte Concurrency- und
Security-Patterns**. Spring Boot gewinnt in beiden:

- Die Kombination aus **JPA-Transaktionen mit Optimistic Locking (`@Version`)** bzw.
  Unique-Constraints für QA-2 sowie **Spring Security OAuth2 Resource Server** für
  die Okta-Integration sind die jeweils ausgereiftesten, am besten dokumentierten
  Lösungen der drei Kandidaten – genau dort, wo NestJS' Auth-Pfad fragmentiert und
  FastAPIs Security-Layer handgestrickter ist.
- **H2** erfüllt das Embedded-DB-Requirement nativ und ohne Reibung.
- Die Konventionen sind seit Jahren stabil und überwältigend in Trainingsdaten
  vertreten → der KI-Agent erzeugt zuverlässig idiomatischen, kohärenten Code.

**FastAPI** ist die starke Zweitwahl (höhere Entwicklungsgeschwindigkeit, ebenfalls
exzellente LLM-Eignung, automatische OpenAPI-Docs) und wäre vorzuziehen, wenn
Iterationsgeschwindigkeit über Auth-Reife stünde. **NestJS** scheidet wegen des
volatilsten Auth-/ORM-Ökosystems und damit höchsten Risikos für inkohärenten Code
aus.

## Konsequenzen

### Positiv

- Reife, deklarative Okta-Anbindung als Resource Server ist vorgezeichnet.
- QA-2 über etablierte Transaktions-/Locking-Patterns gut absicherbar.
- Hohe Vorhersagbarkeit der KI-gestützten Implementierung.
- Migration H2 → PostgreSQL bleibt durch JPA klein.

### Negativ / akzeptierter Trade-off

- Wir tauschen **Entwicklungsgeschwindigkeit und Schlankheit gegen Reife,
  Stabilität und Zuverlässigkeit der Code-Generierung**. Spring Boot bringt die
  höchste Boilerplate-Last und die langsamsten Build-/Startup-Zeiten der drei –
  bewusst in Kauf genommen, weil bei einem KI-implementierten, integritätskritischen
  Service Vorhersagbarkeit und Pattern-Reife wertvoller sind als Knappheit.
- **Embedded-DB als Prototyp-Entscheidung:** H2 (wie auch SQLite) ist single-writer
  und nicht produktionsreif. Für den Produktivbetrieb unter ~300 Nutzern (QA-1) ist
  eine Migration auf einen echten DB-Server (PostgreSQL) einzuplanen.

## Referenzen

- [ADR-001 (arc42): Frontend-Prototyp mit separatem Booking Service](../../arc42/adrs/ADR-001-frontend-prototyp-und-booking-service.md)
- [Qualitätsanforderungen QA-1 / QA-2](../qualitätsanforderungen/README.md)
- [Architekturdokumentation (arc42)](../../arc42/arc42.md)
