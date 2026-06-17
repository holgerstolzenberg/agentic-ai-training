---
Ticket-ID: CLVN-012
Type: Story
Epic: CLVN-007
Status: TODO
---
# Alternative Zeitfenster anzeigen

## User Story

Als INNOQ-Mitarbeiter möchte ich alternative Zeitfenster angezeigt bekommen, damit ich bei Konflikten schnell eine passende Alternative finde.

## Beschreibung

Wenn ein INNOQ-Mitarbeiter einen Konferenzraum für einen bestimmten Zeitraum buchen möchte, dieser aber bereits belegt ist, soll das System automatisch alternative Zeitfenster vorschlagen. Dies verhindert Frustration bei der Buchung und beschleunigt den Prozess, einen passenden Termin zu finden.

Die alternativen Zeitfenster sollten möglichst nah am ursprünglich gewünschten Zeitraum liegen und die gleiche Dauer haben. So kann der Mitarbeiter mit minimalem Aufwand eine geeignete Alternative wählen, ohne manuell verschiedene Zeiträume durchprobieren zu müssen.

Diese Funktion ist Teil des Epics "Auswahl treffen" und baut auf der Verfügbarkeitsprüfung (CLVN-010) sowie der Erkennung belegter Zeitfenster (CLVN-011) auf.

## Akzeptanzkriterien

- [ ] Bei einem Konflikt mit einer bestehenden Buchung werden alternative Zeitfenster angezeigt
- [ ] Mindestens drei alternative Zeitfenster werden vorgeschlagen (sofern verfügbar)
- [ ] Alternative Zeitfenster haben die gleiche Dauer wie der ursprünglich gewünschte Zeitraum
- [ ] Alternativen werden nach zeitlicher Nähe zum Wunschtermin sortiert angezeigt
- [ ] Sowohl frühere als auch spätere Alternativen am selben Tag werden berücksichtigt
- [ ] Ein alternatives Zeitfenster kann direkt ausgewählt werden, um die Buchung fortzusetzen
- [ ] Wenn keine Alternativen am selben Tag verfügbar sind, wird dies dem Mitarbeiter mitgeteilt

## Betroffene Persona

[INNOQ-Mitarbeiter](/docs/produkt/personas/innoq-mitarbeiter.md)

## Zugehöriges Epic

[CLVN-007 - Auswahl treffen](/docs/produkt/backlog/CLVN-007-EPIC-auswahl-treffen.md)
