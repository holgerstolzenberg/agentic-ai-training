import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, of } from 'rxjs';
import { catchError, shareReplay, switchMap } from 'rxjs/operators';
import { CatalogService } from '../../core/catalog.service';
import { BookingService } from '../../core/booking.service';
import { ConferenceRoom, RoomBooking } from '../../core/models';
import { formatDate } from '../../core/format';

/**
 * "Find Rooms" – core feature of the prototype.
 *
 * Covers user stories CLVN-002 (select location),
 * CLVN-003 (show available rooms), CLVN-004 (capacity filter)
 * and CLVN-005 (equipment filter) as well as availability checking
 * for a selected time window.
 */
@Component({
  selector: 'clv-find-rooms',
  imports: [FormsModule],
  templateUrl: './find-rooms.html',
  styleUrl: './find-rooms.scss',
})
export class FindRooms {
  private readonly catalog = inject(CatalogService);
  private readonly booking = inject(BookingService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly locations = toSignal(this.catalog.getLocations(), { initialValue: [] });
  protected readonly equipment = toSignal(this.catalog.getEquipment(), { initialValue: [] });

  /* ---- Filter state (Signals) --------------------------------------- */
  // 'k' (Cologne) is the fallback until the employee home location loads; see constructor
  protected readonly locationId = signal<string>(this.route.snapshot.queryParamMap.get('location') ?? 'k');
  protected readonly minCapacity = signal<number | null>(null);
  protected readonly selectedEquipment = signal<Set<string>>(new Set());
  protected readonly date = signal<string>(this.booking.todayIso);
  protected readonly startTime = signal<string>('09:00');
  protected readonly endTime = signal<string>('10:00');

  protected readonly todayIso = this.booking.todayIso;
  protected formatDate = formatDate;

  constructor() {
    // Update default location from employee's home location once loaded
    const fromQuery = this.route.snapshot.queryParamMap.get('location');
    if (!fromQuery) {
      this.catalog.currentEmployee$.pipe(takeUntilDestroyed()).subscribe((emp) => {
        if (emp) this.locationId.set(emp.homeLocationId);
      });
    }
  }

  /** Rooms at the selected location, updated whenever locationId changes. */
  private readonly allRooms$ = toObservable(this.locationId).pipe(
    switchMap((id) => this.catalog.getRooms(id).pipe(catchError(() => of([])))),
    shareReplay(1),
  );
  private readonly allRooms = toSignal(this.allRooms$, { initialValue: [] });

  /** All confirmed bookings at the selected location for the selected date. */
  private readonly dayBookings$ = combineLatest([toObservable(this.locationId), toObservable(this.date)]).pipe(
    switchMap(([loc, d]) => this.booking.bookingsByLocationAndDate(loc, d).pipe(catchError(() => of([])))),
    shareReplay(1),
  );
  private readonly dayBookings = toSignal(this.dayBookings$, { initialValue: [] as RoomBooking[] });

  /** Is the time window valid for availability checking? */
  protected readonly timeWindowValid = computed(
    () => !!this.date() && !!this.startTime() && !!this.endTime() && this.startTime() < this.endTime(),
  );

  protected readonly selectedLocation = computed(() => this.locations().find((l) => l.id === this.locationId()));

  /** Rooms at the selected location, filtered by capacity and equipment. */
  protected readonly filteredRooms = computed<ConferenceRoom[]>(() => {
    const min = this.minCapacity();
    const features = this.selectedEquipment();
    return this.allRooms()
      .filter((r) => min == null || r.capacity >= min)
      .filter((r) => [...features].every((m) => r.equipmentIds.includes(m)))
      .sort((a, b) => a.capacity - b.capacity);
  });

  protected readonly activeFilterCount = computed(
    () => (this.minCapacity() != null ? 1 : 0) + this.selectedEquipment().size,
  );

  /* ---- Room selection (CLVN-016) ------------------------------------- */
  protected readonly selectedRoom = signal<ConferenceRoom | null>(null);

  protected readonly selectedRoomLocation = computed(() => {
    const room = this.selectedRoom();
    return room ? this.locations().find((l) => l.id === room.locationId) : null;
  });

  protected selectRoom(room: ConferenceRoom): void {
    this.selectedRoom.set(room);
  }

  protected confirmSelection(): void {
    const room = this.selectedRoom();
    if (!room) return;
    this.router.navigate(['/room', room.id], { queryParams: this.detailParams() });
  }

  /* ---- Actions ------------------------------------------------------ */
  protected toggleEquipment(id: string): void {
    this.selectedEquipment.update((set) => {
      const next = new Set(set);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  }

  protected isSelected(id: string): boolean {
    return this.selectedEquipment().has(id);
  }

  protected resetFilters(): void {
    this.minCapacity.set(null);
    this.selectedEquipment.set(new Set());
  }

  /** Bookings grouped by roomId, sorted by startTime — computed once per dayBookings change. */
  private readonly bookingsByRoomId = computed<Map<string, RoomBooking[]>>(() => {
    const map = new Map<string, RoomBooking[]>();
    for (const b of this.dayBookings()) {
      const list = map.get(b.roomId) ?? [];
      list.push(b);
      map.set(b.roomId, list);
    }
    for (const list of map.values()) {
      list.sort((a, b) => a.startTime.localeCompare(b.startTime));
    }
    return map;
  });

  /* ---- Per-room availability (from preloaded day bookings) ---------- */
  protected isAvailable(room: ConferenceRoom): boolean {
    if (!this.timeWindowValid()) return true;
    const start = this.startTime();
    const end = this.endTime();
    return !(this.bookingsByRoomId().get(room.id) ?? []).some((b) => b.startTime < end && b.endTime > start);
  }

  protected getBookingsForRoom(room: ConferenceRoom): RoomBooking[] {
    return this.bookingsByRoomId().get(room.id) ?? [];
  }

  protected equipmentIcon(id: string): string {
    return this.equipment().find((e) => e.id === id)?.icon ?? '•';
  }
  protected equipmentName(id: string): string {
    return this.equipment().find((e) => e.id === id)?.name ?? id;
  }

  /** Query params to pass the time window to the detail/booking page. */
  protected detailParams(): Record<string, string> {
    return { date: this.date(), start: this.startTime(), end: this.endTime() };
  }
}
