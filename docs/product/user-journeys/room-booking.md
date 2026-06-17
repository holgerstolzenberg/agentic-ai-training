# User Journey: Einen Raum buchen

## Übersicht
Dieses Dokument beschreibt die User Journey für die Buchung eines Raumes mit dem Calvin Raumbuchungssystem, von der initialen Bedarfsidentifikation bis zur erfolgreichen Buchungsbestätigung.

## User Journey Ablauf

```mermaid
journey
    title User Journey: Raumbuchung in Calvin

    section Bedarf Identifizieren
      Kalender einsehen: 4: User
      Bedarf für Meetingraum erkennen: 4: User

    section Räume finden
      Bürostandort auswählen: 4: User
      Räume am Standort einsehen: 3: User

    section Auswahl treffen
      Gewünschtes Zeitfenster auswählen: 3: User, System
      Raumverfügbarkeit prüfen: 2: User, System
      Raumausstattung prüfen (Beamer, Flipchart): 3: User

    section Raum buchen
      Raumauswahl bestätigen: 4: User
      Buchungsanfrage absenden: 3: User, System
      Buchungsbestätigung erhalten: 5: User

    section Buchungen verwalten
      Seite "Meine Buchungen" aufrufen: 4: User
      Bestätigte Buchung ansehen: 4: User
      Raum sicher gebucht: 5: User, System
```