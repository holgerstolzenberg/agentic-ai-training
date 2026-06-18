import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { map } from 'rxjs/operators';
import { CatalogService } from '../../core/catalog.service';
import { BookingService } from '../../core/booking.service';
import { formatDate } from '../../core/format';

/** Home page: product vision, key metrics, and entry points to entities. */
@Component({
  selector: 'clv-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private readonly catalog = inject(CatalogService);
  protected readonly booking = inject(BookingService);

  protected readonly locationCount = toSignal(this.catalog.getLocations().pipe(map((l) => l.length)), {
    initialValue: 0,
  });
  protected readonly roomCount = toSignal(this.catalog.getRooms().pipe(map((r) => r.length)), { initialValue: 0 });

  /** The employee's next upcoming booking (from today onwards). */
  protected readonly nextBooking = computed(() => {
    const today = this.booking.todayIso;
    return this.booking.myBookings().find((b) => b.date >= today) ?? null;
  });

  protected formatDate = formatDate;
}
