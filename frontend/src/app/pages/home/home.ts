import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CatalogService } from '../../core/catalog.service';
import { BookingService } from '../../core/booking.service';
import { formatDatum } from '../../core/format';

/** Startseite: Produktvision, Kennzahlen und Einstieg in die Entitäten. */
@Component({
  selector: 'clv-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private readonly catalog = inject(CatalogService);
  protected readonly booking = inject(BookingService);

  protected readonly anzahlStandorte = this.catalog.getStandorte().length;
  protected readonly anzahlRaeume = this.catalog.getRaeume().length;

  /** Nächste anstehende Buchung des Mitarbeiters (ab heute). */
  protected readonly naechsteBuchung = computed(() => {
    const heute = this.booking.heuteIso;
    return this.booking.meineBuchungen().find((b) => b.datum >= heute) ?? null;
  });

  protected raumName(raumId: string): string {
    return this.catalog.getRaum(raumId)?.name ?? raumId;
  }
  protected standortName(standortId: string): string {
    return this.catalog.getStandort(standortId)?.name ?? standortId;
  }
  protected formatDatum = formatDatum;
}
