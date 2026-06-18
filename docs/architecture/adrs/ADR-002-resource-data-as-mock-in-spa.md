# ADR-002: Resource Data as Mock Data in the SPA (no separate Resource Service)

**Status:** Superseded (2026-06-18) — resource data migrated to the Booking Service backend as part of CLVN-016
**Date:** 2026-06-17

## Kontext und Problemstellung

Calvin verwaltet zwei Arten von Daten: **Stamm-/Ressourcendaten** (Standorte,
Konferenzräume, Ausstattung) und **Buchungsdaten** (Raumbuchungen). Bisher war
offen, ob die Ressourcendaten von einem eigenen Service bereitgestellt werden.

Für die Prototyping-Phase wollen wir den Aufwand minimieren und keine zusätzliche
Komponente (Service + Datenbank + API) nur für weitgehend statische Stammdaten
betreiben. Der Frontend-Prototyp hält diese Daten bereits als Mock-Daten vor.

## Entscheidung

- Der **Resource-Service wird in die SPA integriert**: Standorte, Räume und
  Ausstattung werden als **Mock-Daten in der SPA** hinterlegt
  (`frontend/src/app/core/mock-data.ts`).
- Der **Booking-Service arbeitet ausschließlich mit den IDs** aus diesen
  Mock-Daten. Er speichert und validiert Buchungen, kennt die Ressourcen aber nur
  über ihre IDs (Standort-ID, Raum-ID, Ausstattung-IDs) und verwaltet die
  Stammdaten selbst nicht.

## Begründung

- **Schnelle Entwicklung:** Keine zweite Backend-Komponente, kein eigenes
  Stammdaten-Schema, keine zusätzliche API.
- **Wiederverwendung:** Der bestehende, bereits validierte Frontend-Prototyp nutzt
  diese Mock-Daten schon.
- **Klarer Schnitt:** Die Buchungslogik (das eigentliche fachliche Risiko, siehe
  QA-2) bleibt im Booking-Service isoliert; Stammdaten sind für den Prototyp
  bewusst „dumm".

## Konsequenzen

### Positiv

- Minimaler Aufwand, schneller lauffähiger Prototyp.
- Booking-Service bleibt schlank (nur Buchungen + IDs).

**Negativ (technische Schuld, siehe [Technische Schulden](../technische-schulden.md))**

- Keine zentrale, serverseitige Pflege der Ressourcendaten.
- **Referenzielle Integrität** (existiert eine Raum-/Standort-ID wirklich?) wird
  serverseitig **nicht erzwungen** – der Booking-Service vertraut den IDs der SPA.
- Bei späterer Einführung eines echten Resource-Service drohen **Datenredundanz**
  und Migrationsaufwand.

## Referenzen

- [ADR-001: Technologie-Stack für den Booking Service](./ADR-001-technologie-stack-fuer-booking-service.md)
- [ADR-001 (arc42): Frontend-Prototyp mit separatem Booking Service](../../arc42/adrs/ADR-001-frontend-prototyp-und-booking-service.md)
- [Technische Schulden](../technische-schulden.md)
