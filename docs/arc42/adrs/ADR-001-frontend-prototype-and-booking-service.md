# ADR-001: Frontend-Prototyp mit separatem Booking Service

**Status**: Akzeptiert

## Kontext

Es existiert ein Frontend-Prototyp (React, TypeScript, Tailwind CSS, ShadCN UI). Für den produktiven Betrieb muss die Buchungslogik serverseitig umgesetzt werden.

## Entscheidung

Wir behalten den Frontend-Prototypen und ergänzen ihn um einen **Booking Service** als Backend. Kommunikation erfolgt über eine **REST API (JSON über HTTPS)**. Die Buchungslogik liegt vollständig im Booking Service.

## Begründung

- Doppelbuchungen (QS-2: 99,9% Prävention) erfordern serverseitige Transaktionsmechanismen — im Browser nicht zuverlässig umsetzbar.
- Performance (QS-1: < 500ms bei 150 Nutzern) erfordert serverseitige Datenabfrage.
- Umbau des validierten Frontend-Prototypen wäre Aufwand ohne Mehrwert.
