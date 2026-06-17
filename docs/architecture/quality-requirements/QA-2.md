# QA-2 · Verhinderung von Doppelbuchungen

**Qualitätsmerkmal:** Zuverlässigkeit / Datenintegrität
**Priorität:** 1 von 5

**Szenario (Prosa):**
> Im Normalbetrieb versuchen zwei INNOQ-Mitarbeiter nahezu zeitgleich (innerhalb
> derselben Sekunde), denselben Konferenzraum für ein überlappendes Zeitfenster zu
> buchen. Der Booking Service **bestätigt die erste vollständige Buchungsanfrage und
> lehnt die zweite mit einer verständlichen Meldung ab; eine Doppelbuchung wird in
> ≥ 99,9 % der konkurrierenden Fälle serverseitig verhindert.**

| Baustein | Ausprägung |
|----------|------------|
| **Environment** | Normalbetrieb, konkurrierende Buchungsversuche innerhalb derselben Sekunde |
| **Source** | Zwei INNOQ-Mitarbeiter gleichzeitig |
| **Event** | Buchen denselben Konferenzraum für ein überlappendes Zeitfenster |
| **Artifact** | Booking Service (serverseitige Konfliktprüfung / Verfügbarkeitsanzeige) |
| **Response** | Erste Anfrage wird bestätigt, zweite mit verständlicher Fehlermeldung abgelehnt; keine widersprüchliche Buchung wird persistiert |
| **Measure** | Doppelbuchung in ≥ 99,9 % der konkurrierenden Fälle verhindert; Ablehnung mit Antwort in < 1 s |

**Motivation:** Die Vermeidung von Doppelbuchungen ist das Kernversprechen von
Calvin und Grundlage des Nutzervertrauens („Sicherheit, dass der Raum wirklich frei ist“).

---

[← Übersicht Qualitätsanforderungen](./README.md)
