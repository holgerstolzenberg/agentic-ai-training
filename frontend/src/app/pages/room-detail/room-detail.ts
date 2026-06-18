import { Component, computed, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { combineLatest, of } from 'rxjs';
import { catchError, debounceTime, filter, startWith, switchMap } from 'rxjs/operators';
import { CatalogService } from '../../core/catalog.service';
import { BookingService } from '../../core/booking.service';
import { Availability, RoomBooking } from '../../core/models';
import { formatDate } from '../../core/format';

/**
 * View room details (CLVN-006) and book a room.
 *
 * Displays capacity, equipment and location, the day's occupancy, and a
 * booking form with live availability checking. Double bookings are
 * prevented (Glossary: "Doppelbuchung", "Verfügbarkeit").
 */
@Component({
  selector: 'clv-room-detail',
  imports: [FormsModule, RouterLink],
  templateUrl: './room-detail.html',
  styleUrl: './room-detail.scss',
})
export class RoomDetail {
  private readonly catalog = inject(CatalogService);
  private readonly booking = inject(BookingService);
  private readonly route = inject(ActivatedRoute);

  private readonly roomId = this.route.snapshot.paramMap.get('id') ?? '';

  protected readonly room = toSignal(this.catalog.getRoom(this.roomId).pipe(catchError(() => of(null))), {
    initialValue: null,
  });

  protected readonly location = toSignal(
    toObservable(this.room).pipe(
      filter((r) => !!r),
      switchMap((r) => this.catalog.getLocation(r!.locationId)),
    ),
    { initialValue: undefined },
  );

  private readonly allEquipment = toSignal(this.catalog.getEquipment(), { initialValue: [] });

  protected readonly todayIso = this.booking.todayIso;
  protected formatDate = formatDate;

  /* ---- Form state ---------------------------------------------------- */
  protected readonly date = signal<string>(this.route.snapshot.queryParamMap.get('date') ?? this.booking.todayIso);
  protected readonly startTime = signal<string>(this.route.snapshot.queryParamMap.get('start') ?? '09:00');
  protected readonly endTime = signal<string>(this.route.snapshot.queryParamMap.get('end') ?? '10:00');
  protected readonly title = signal<string>('');
  protected readonly note = signal<string>('');

  /** Successfully created booking (booking confirmation). */
  protected readonly confirmation = signal<RoomBooking | null>(null);
  protected readonly error = signal<string | null>(null);

  /* ---- Derived state ------------------------------------------------- */
  protected readonly dayBookings = toSignal(
    toObservable(this.date).pipe(
      switchMap((d) => this.booking.bookingsForRoom(this.roomId, d).pipe(catchError(() => of([])))),
    ),
    { initialValue: [] as RoomBooking[] },
  );

  protected readonly availability = toSignal(
    combineLatest([toObservable(this.date), toObservable(this.startTime), toObservable(this.endTime)]).pipe(
      debounceTime(200),
      switchMap(([d, s, e]) =>
        this.booking.checkAvailability(this.roomId, d, s, e).pipe(
          startWith(null),
          catchError(() => of<Availability>({ available: false, conflicts: [] })),
        ),
      ),
    ),
    { initialValue: null as Availability | null },
  );

  protected readonly timeValid = computed(() => this.startTime() < this.endTime());

  protected readonly canBook = computed(
    () => this.timeValid() && this.availability()?.available === true && this.title().trim().length > 0,
  );

  protected readonly equipment = computed(() => {
    const items = this.allEquipment();
    return (this.room()?.equipmentIds ?? []).map((id) => items.find((e) => e.id === id)).filter(Boolean);
  });

  /* ---- Actions ------------------------------------------------------ */
  protected submitBooking(): void {
    const room = this.room();
    if (!room) return;
    this.error.set(null);
    this.booking
      .bookRoom({
        roomId: room.id,
        locationId: room.locationId,
        date: this.date(),
        startTime: this.startTime(),
        endTime: this.endTime(),
        title: this.title().trim(),
        note: this.note().trim() || undefined,
      })
      .subscribe({
        next: (b) => this.confirmation.set(b),
        error: (err) => this.error.set(err.error?.detail ?? err.message ?? 'The booking could not be created.'),
      });
  }

  protected resetBooking(): void {
    this.confirmation.set(null);
    this.title.set('');
    this.note.set('');
  }
}
