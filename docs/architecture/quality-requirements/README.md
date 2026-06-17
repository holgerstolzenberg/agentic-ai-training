# Qualitätsanforderungen: Calvin

Dieser Ordner beschreibt die wesentlichen **Qualitätsszenarien** für das
Calvin-Raumbuchungssystem (arc42, [Kapitel 10](https://docs.arc42.org/section-10/)).
Jedes Szenario liegt in einer eigenen Datei (`QA-1.md` … `QA-5.md`).

Die Szenarien folgen einem einheitlichen Template aus sechs Bausteinen:

> **&lt;Environment&gt; &lt;Source&gt; &lt;Event&gt; &lt;Artifact&gt; &lt;Response&gt; &lt;Measure&gt;**

| Baustein | Bedeutung |
|----------|-----------|
| **Environment** | Betriebssituation/-zustand, in der das Szenario eintritt (z. B. Normalbetrieb, Lastspitze, Erstnutzung) |
| **Source** | Auslöser des Reizes (z. B. Mitarbeiter, Entwickler, zweiter Nutzer) |
| **Event** | Der Reiz/das Ereignis (was passiert) |
| **Artifact** | Betroffener Systembestandteil (z. B. SPA, Booking Service) |
| **Response** | Reaktion des Systems auf den Reiz |
| **Measure** | Messbares Akzeptanzkriterium der Reaktion |

> **Beispiel (Vorlage):** *In normal use, a consultant views the bookings of an
> office on the Calvin Website. The bookings are visible and interactive (first
> contentful paint) in 300 ms for 95 % of the requests.*

Begriffe entsprechen dem [Glossar](../../produkt/glossar.md) (Ubiquitous Language):
*Standort, Konferenzraum, Raumbuchung, Verfügbarkeit, Doppelbuchung, Buchungsübersicht.*

Kalibrierung der Messwerte: ausgelegt für **bis zu ~300 gleichzeitige Nutzer**
(Wachstumsreserve gegenüber den heute ~150 Nutzern).

## Qualitätsbaum (Priorisierung)

| Priorität | Szenario | Qualitätsmerkmal | Titel |
|-----------|----------|------------------|-------|
| 1 | [QA-2](./QA-2.md) | Zuverlässigkeit / Datenintegrität | Verhinderung von Doppelbuchungen |
| 2 | [QA-1](./QA-1.md) | Performance / Antwortzeit | Performance bei der Raumanzeige |
| 3 | [QA-3](./QA-3.md) | Benutzbarkeit / Erlernbarkeit | Erste Buchung ohne Schulung |
| 4 | [QA-4](./QA-4.md) | Sicherheit / Datenschutz | Anonyme Sichtbarkeit fremder Buchungen |
| 5 | [QA-5](./QA-5.md) | Änderbarkeit / Erweiterbarkeit | Integration eines neuen Standorts |

---

*Quellen & Methodik:* arc42 [Kapitel 10 – Qualitätsanforderungen](https://docs.arc42.org/section-10/).
Die hier ausformulierten Szenarien ergänzen die priorisierten Qualitätsziele in der
[Architekturdokumentation](../../arc42/arc42.md#qualitätsanforderungen).
