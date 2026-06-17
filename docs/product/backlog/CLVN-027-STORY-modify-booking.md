---
Ticket-ID: CLVN-027
Type: Story
Epic: CLVN-021
Status: TODO
---
# Buchung ändern

## User Story

Als INNOQ-Mitarbeiter möchte ich eine Raumbuchung ändern oder verschieben, damit ich flexibel auf Terminänderungen reagieren kann.

## Beschreibung

Im Arbeitsalltag eines INNOQ-Mitarbeiters kommt es häufig vor, dass sich Termine verschieben oder die Anforderungen an ein Meeting ändern. Ein geplantes Team-Meeting wird um eine Stunde verschoben, ein Workshop benötigt plötzlich mehr Zeit, oder die Teilnehmerzahl steigt und ein größerer Raum wird benötigt.

Die Möglichkeit, bestehende Raumbuchungen flexibel anzupassen, spart dem Mitarbeiter den Aufwand, eine Buchung zu stornieren und komplett neu anzulegen. Gleichzeitig bleibt die Buchungshistorie nachvollziehbar. Die Änderungsfunktion prüft automatisch die Verfügbarkeit für den neuen Zeitraum oder Raum und verhindert so Doppelbuchungen.

Diese Funktion unterstützt insbesondere INNOQ-Mitarbeiter, die ihre seltenen Bürotage optimal nutzen möchten und bei Terminänderungen schnell reagieren müssen.

## Akzeptanzkriterien

- [ ] Eine bestehende Raumbuchung kann über die Buchungsdetails zur Bearbeitung geöffnet werden
- [ ] Das Datum der Buchung kann geändert werden
- [ ] Die Uhrzeit (Start- und Endzeit) der Buchung kann angepasst werden
- [ ] Der gebuchte Konferenzraum kann gewechselt werden (unter Beibehaltung des Standorts)
- [ ] Bei Änderung wird die Verfügbarkeit des Raums für den neuen Zeitraum geprüft
- [ ] Bei Konflikten (Raum bereits belegt) wird eine entsprechende Meldung angezeigt
- [ ] Änderungen können vor dem Speichern verworfen werden
- [ ] Nach erfolgreicher Änderung wird eine Bestätigung angezeigt
- [ ] Die geänderte Buchung wird in der Buchungsübersicht aktualisiert dargestellt

## Betroffene Persona

[INNOQ-Mitarbeiter](/docs/produkt/personas/innoq-mitarbeiter.md)

## Zugehöriges Epic

[CLVN-021 - Buchungen verwalten](/docs/produkt/backlog/CLVN-021-EPIC-buchungen-verwalten.md)
