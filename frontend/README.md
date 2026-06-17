# Calvin – Frontend (Angular SPA)

Interner Konferenzraum-Buchungsprototyp für INNOQ. Reine Browser-SPA
(Angular 22, Standalone-Components + Signals).

> Vision: *Mühelos den passenden Raum finden und buchen – an jedem INNOQ-Standort.*

## Starten

```bash
cd frontend
npm install      # einmalig
```

| Modus | Befehl | Beschreibung |
|-------|--------|--------------|
| Mit Backend | `npm run start:backend` | Dev-Server auf <http://localhost:4200>, leitet `/api/v1/*` per Proxy an den Booking-Service (`:8080`) weiter |
| Ohne Backend | `npm start` | Dev-Server ohne Proxy, nur mit Mock-Daten |
| Produktions-Build | `npm run build` | Build nach `dist/calvin` |
| Tests | `npm test` | Unit-Tests (benötigt Chrome/Chromium) |

Der Proxy ist in `proxy.conf.json` konfiguriert und leitet alle Anfragen
unter `/api/v1` an `http://localhost:8080` weiter. Der Booking-Service muss
dafür laufen (`cd backend && ./mvnw spring-boot:start`).

## Design / Corporate Identity

Abgeleitet aus der Produktions-CSS von [innoq.com](https://www.innoq.com/en/):

| Rolle    | Farbe              |
|----------|--------------------|
| Primär   | Petrol `#004153`   |
| Akzent   | Apricot `#ff9c66`  |
| Tinte    | `#242424`          |
| Signal   | Frei `#009999`, Belegt `#d01040` |

Schrift: geometrische Sans (FF Mark) → Fallback `"Helvetica Neue", Arial`.
Die Design-Tokens liegen zentral in `src/styles.scss` (`--clv-*`).

## Architektur

```text
src/app/
  core/                 Domänenmodell, Services, Backend-Anbindung
    models.ts           Entitäten gemäß Glossar (Ubiquitous Language)
    mock-data.ts        Standorte, Räume, Ausstattung, Buchungen (Mock)
    catalog.service.ts  Read-only Stammdaten
    booking.service.ts  Buchungs-State, Verfügbarkeit, Doppelbuchungsschutz
    backend.service.ts  HTTP-Zugriff auf den Booking-Service (/api/v1)
    health.service.ts   Konnektivitätsprüfung via Spring Actuator Health
  pages/                je Seite eine lazy-geladene Standalone-Component
  app.{ts,html,scss}    Shell: INNOQ-Header + Navigation nach Entitäten
```

Die **Hauptnavigation** folgt den Entitäten aus dem Glossar:
*Räume finden* (Konferenzraum) · *Standorte* · *Meine Buchungen*
(Raumbuchung) · *Arbeitsplätze* (Ausblick).

## Abgedeckte User Stories

| Story | Feature im Prototyp |
|-------|---------------------|
| CLVN-002 Standort auswählen | Standort-Auswahl in „Räume finden" + Standorte-Seite |
| CLVN-003 Verfügbare Räume anzeigen | Raumliste mit Eckdaten & Verfügbarkeits-Badge, Leerzustand |
| CLVN-004 Nach Kapazität filtern | Mindestkapazität-Filter (kombinierbar) |
| CLVN-005 Nach Ausstattung filtern | Multi-Select Ausstattungsfilter (kombinierbar) |
| CLVN-006 Raumdetails einsehen | Detailseite: Kapazität, Ausstattung, Lage, Tagesbelegung |
| Auswahl treffen (Map) | Datum + Start/Endzeit, Live-Verfügbarkeit, belegte Zeitfenster |
| Raum buchen (Map) | Buchungsformular, Meetingtitel/Notiz, Bestätigung |
| Buchungen verwalten (Map) | „Meine Buchungen" inkl. Stornierung |

Doppelbuchungen werden verhindert: eine Buchung mit überlappendem
Zeitfenster wird abgelehnt (siehe `BookingService.bucheRaum`).
