import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BookingService } from '../../core/booking.service';
import { RoomBooking } from '../../core/models';
import { formatDate } from '../../core/format';

/**
 * "My Bookings" – overview of all own room bookings with
 * cancellation option (User Story Map: manage bookings).
 */
@Component({
  selector: 'clv-my-bookings',
  imports: [RouterLink],
  templateUrl: './my-bookings.html',
  styleUrl: './my-bookings.scss',
})
export class MyBookings {
  protected readonly booking = inject(BookingService);

  protected readonly cancelError = signal<string | null>(null);
  protected formatDate = formatDate;

  /** Upcoming bookings from today onwards. */
  protected readonly upcomingBookings = computed(() =>
    this.booking.myBookings().filter((b) => b.date >= this.booking.todayIso),
  );

  /** Past bookings. */
  protected readonly pastBookings = computed(() =>
    this.booking.myBookings().filter((b) => b.date < this.booking.todayIso).reverse(),
  );

  protected tag(iso: string): string {
    return iso.slice(8, 10);
  }
  protected shortMonth(iso: string): string {
    return new Intl.DateTimeFormat('en-US', { month: 'short' }).format(new Date(iso + 'T00:00:00'));
  }

  protected cancelBooking(b: RoomBooking): void {
    if (confirm(`Really cancel booking "${b.title}" on ${formatDate(b.date)}?`)) {
      this.cancelError.set(null);
      this.booking.cancel(b.id).subscribe({
        error: (err) => this.cancelError.set(err.error?.detail ?? err.message ?? 'Cancellation failed.'),
      });
    }
  }
}
