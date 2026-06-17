# Technische Schulden: Calvin

Dieses Dokument führt die **bewusst eingegangenen technischen Schulden** des Calvin-
Prototyps. Sie sind das Ergebnis von Architekturentscheidungen, die kurzfristig
Geschwindigkeit über Vollständigkeit/Produktionsreife stellen. Jede Schuld nennt
Ursache, Risiko und einen geplanten Tilgungspfad.

> Konvention: Schulden werden vor dem Produktivgang abgebaut bzw. neu bewertet.
> „Blocker für Produktion" markiert Schulden, die einem Produktivbetrieb entgegenstehen.

| ID | Technische Schuld | Ursache / Kontext | Risiko / Auswirkung | Geplante Tilgung | Referenz |
|----|-------------------|-------------------|---------------------|------------------|----------|
| **TS-1** | Keine echte Authentifizierung – **Basic-Auth ohne Passwörter** statt Okta | Schnelles Multi-User-Testing, keine Drittsystem-Abhängigkeit im Prototyp | **Blocker für Produktion:** Jede Person kann sich als beliebiger Nutzer ausgeben; Zugriffskontrolle (QA-4) nur „kooperativ", nicht sicher durchsetzbar | **Okta-Integration (OIDC/OAuth2) vor Produktivgang** nachliefern; Stack (Spring Security) ist vorbereitet | [ADR-003](./adrs/ADR-003-authentifizierung-basic-auth-ohne-passwoerter.md), [ADR-001](./adrs/ADR-001-technologie-stack-fuer-booking-service.md) |
| **TS-2** | **Ressourcendaten als Mock-Daten in der SPA**, kein Resource-Service | Aufwand sparen; Stammdaten sind weitgehend statisch; Prototyp nutzt sie bereits | Keine zentrale Pflege; **referenzielle Integrität der IDs serverseitig nicht erzwungen**; Redundanz-/Migrationsaufwand bei späterem echten Service | Bei Bedarf echten Resource-Service inkl. Persistenz und ID-Validierung einführen | [ADR-002](./adrs/ADR-002-ressourcendaten-als-mock-in-der-spa.md) |
| **TS-3** | **Dateibasierte Datenbank (H2)** für Buchungen | Embedded DB ohne separaten DB-Server für den Prototyp | Single-Writer, nicht produktionsreif; begrenzte Nebenläufigkeit unter Last (QA-1/QA-2 bei ~300 Nutzern) | **Migration auf PostgreSQL** vor Produktivbetrieb (durch JPA gering gehalten) | [ADR-001](./adrs/ADR-001-technologie-stack-fuer-booking-service.md) |
| **TS-4** | **Keine responsive Darstellung** der SPA | Prototyp-Fokus auf Funktion; Responsiveness war zunächst nicht im Scope | Eingeschränkte Nutzbarkeit auf kleinen/mobilen Viewports | Responsive-Ausbau (Layout/Breakpoints) nach der Prototyp-Phase | – |
| **TS-5** | **In-Memory-Buchungsdaten im Frontend-Prototyp** | Der SPA-Prototyp mockt Buchungen lokal, solange der Booking-Service nicht angebunden ist | Buchungen sind nicht persistent und nicht nutzerübergreifend; nur zur UI-Demonstration geeignet | Anbindung der SPA an den **Booking-Service** (REST) statt der lokalen Mock-Buchungen | [ADR-001 (arc42)](../arc42/adrs/ADR-001-frontend-prototyp-und-booking-service.md) |

## Hinweise zur Steuerung

- **TS-1** und **TS-3** sind **Blocker für den Produktivbetrieb** und müssen vor dem
  Go-Live abgebaut werden.
- **TS-2** und **TS-5** sind tragbar, solange Calvin Prototyp bleibt; sie sind bei
  einer Produktentscheidung neu zu bewerten.
- Neue Schulden werden hier mit fortlaufender ID (TS-n) ergänzt und – wo sinnvoll –
  mit einem ADR begründet.

## Referenzen

- [Architekturdokumentation (arc42)](../arc42/arc42.md)
- [Architekturentscheidungen (ADRs)](./adrs/)
- [Qualitätsanforderungen](./qualitätsanforderungen/README.md)
