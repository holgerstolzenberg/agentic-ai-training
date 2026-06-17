---
Ticket-ID: CLVN-019
Type: Story
Epic: CLVN-015
Status: TODO
---
# Raumbuchung absenden

## User Story

Als INNOQ-Mitarbeiter möchte ich meine Raumbuchung absenden, damit die Reservierung verbindlich wird.

## Beschreibung

Nachdem ein INNOQ-Mitarbeiter einen passenden Konferenzraum ausgewählt, einen Meetingtitel eingegeben und optional eine Buchungsnotiz hinzugefügt hat, möchte er die Buchung final absenden. Das Absenden der Raumbuchung macht die Reservierung verbindlich und blockiert den Konferenzraum für den gewählten Zeitraum. Dadurch wird sichergestellt, dass keine Doppelbuchung entstehen kann und der Mitarbeiter Planungssicherheit für sein Meeting oder seinen Workshop erhält.

Diese User Story ist ein zentraler Schritt im Buchungsprozess und stellt den Übergang von der Buchungsvorbereitung zur verbindlichen Reservierung dar.

## Akzeptanzkriterien

- [ ] Ein Button "Buchung absenden" ist sichtbar und klickbar, wenn alle Pflichtfelder ausgefüllt sind
- [ ] Die Raumbuchung wird nach dem Absenden im System gespeichert
- [ ] Der Konferenzraum wird für den gewählten Zeitraum als belegt markiert
- [ ] Eine Doppelbuchung für denselben Zeitraum wird verhindert
- [ ] Bei erfolgreicher Buchung wird der Mitarbeiter zur Buchungsbestätigung weitergeleitet
- [ ] Bei einem Fehler (z.B. Raum zwischenzeitlich gebucht) wird eine verständliche Fehlermeldung angezeigt
- [ ] Der Button ist deaktiviert, solange Pflichtfelder nicht ausgefüllt sind

## Betroffene Persona

[INNOQ-Mitarbeiter](/docs/produkt/personas/innoq-mitarbeiter.md)

## Zugehöriges Epic

[CLVN-015 - Raum buchen](/docs/produkt/backlog/CLVN-015-EPIC-raum-buchen.md)
