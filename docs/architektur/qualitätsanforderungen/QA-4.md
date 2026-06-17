# QA-4 · Anonyme Sichtbarkeit fremder Buchungen

**Qualitätsmerkmal:** Sicherheit / Datenschutz
**Priorität:** 4 von 5

**Szenario (Prosa):**
> Im Normalbetrieb öffnet ein eingeloggter INNOQ-Mitarbeiter die Buchungsübersicht
> bzw. Tagesbelegung eines Konferenzraums, der von einer anderen Person gebucht
> wurde. Das System zeigt **fremde Zeitfenster ausschließlich anonym als „belegt“;
> Meetingtitel, Notiz und Name sind nur bei eigenen Buchungen sichtbar – in 100 % der
> Zugriffe werden zu fremden Buchungen keine personenbezogenen Daten ausgeliefert.**

| Baustein | Ausprägung |
|----------|------------|
| **Environment** | Normalbetrieb, angemeldeter Mitarbeiter betrachtet eine fremde Belegung |
| **Source** | INNOQ-Mitarbeiter (nicht der Buchende), identifiziert über den Basic-Auth-Benutzernamen |
| **Event** | Öffnet die Buchungsübersicht / Tagesbelegung eines Raums an einem Standort |
| **Artifact** | Calvin Web-App + Booking Service (Autorisierung der ausgelieferten Felder anhand des angemeldeten Nutzers) |
| **Response** | Fremde Slots erscheinen nur als „belegt“ (ohne Titel/Notiz/Person); vollständige Details nur bei eigenen Buchungen |
| **Measure** | In 100 % der Zugriffe keine personenbezogenen Daten (Name, Meetingtitel, Notiz) zu fremden Buchungen; abgesichert durch automatisierte Tests und Reviews |

**Motivation:** Transparenz über Verfügbarkeit darf nicht zu Lasten des
Persönlichkeitsschutzes gehen. Mitarbeiter sehen, *dass* belegt ist, aber nicht
*wer was* macht – das wahrt Vertrauen und Datensparsamkeit.

> **Hinweis (Prototyp):** Die Identität stammt im Prototyp aus **Basic-Auth ohne
> Passwörter** ([ADR-003](../adrs/ADR-003-authentifizierung-basic-auth-ohne-passwoerter.md)).
> Die Feld-Anonymisierung ist umgesetzt, eine **sichere** Durchsetzung dieses
> Szenarios setzt jedoch die spätere Okta-Integration voraus (siehe
> [Technische Schulden TS-1](../technische-schulden.md)).

---

[← Übersicht Qualitätsanforderungen](./README.md)
