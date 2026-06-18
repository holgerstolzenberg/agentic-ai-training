import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { RoomBooking, Availability } from './models';

/** Input data for a new room booking. */
export interface BookingRequest {
  roomId: string;
  locationId: string;
  date: string;
  startTime: string;
  endTime: string;
  title: string;
  note?: string;
}

/**
 * Manages room bookings via the Booking Service HTTP API.
 * Local signal state is kept in sync via tap() after each mutation.
 */
@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly http = inject(HttpClient);

  /** Today's date – fixed once at startup. */
  readonly today = new Date();

  private readonly _bookings = signal<RoomBooking[]>([]);

  /** Confirmed bookings of the current employee, in chronological order. */
  readonly myBookings = computed(() =>
    this._bookings()
      .filter((b) => b.status === 'confirmed')
      .sort((a, b) => (a.date + a.startTime).localeCompare(b.date + b.startTime)),
  );

  /** Number of confirmed bookings for the current employee. */
  readonly myBookingCount = computed(() => this.myBookings().length);

  /** Today's date as an ISO string YYYY-MM-DD. */
  get todayIso(): string {
    return this.today.toISOString().slice(0, 10);
  }

  constructor() {
    this.http.get<RoomBooking[]>('api/v1/bookings').subscribe((bookings) => {
      this._bookings.set(bookings);
    });
  }

  /** Confirmed bookings for a room on a given date, sorted by start time. */
  bookingsForRoom(roomId: string, date: string): Observable<RoomBooking[]> {
    return this.http.get<RoomBooking[]>('api/v1/bookings', {
      params: { 'room-id': roomId, date },
    });
  }

  /** All confirmed bookings for a location on a given date. */
  bookingsByLocationAndDate(locationId: string, date: string): Observable<RoomBooking[]> {
    return this.http.get<RoomBooking[]>('api/v1/bookings', {
      params: { 'location-id': locationId, date },
    });
  }

  /** Checks availability of a room for a time window. */
  checkAvailability(roomId: string, date: string, startTime: string, endTime: string): Observable<Availability> {
    return this.http.get<Availability>('api/v1/availability', {
      params: { 'room-id': roomId, date, 'start-time': startTime, 'end-time': endTime },
    });
  }

  /** Creates a new room booking. Returns the created booking on success. */
  bookRoom(request: BookingRequest): Observable<RoomBooking> {
    return this.http
      .post<RoomBooking>('api/v1/bookings', request)
      .pipe(tap((booking) => this._bookings.update((list) => [...list, booking])));
  }

  /** Cancels a booking. */
  cancel(bookingId: string): Observable<void> {
    return this.http
      .delete<void>(`api/v1/bookings/${bookingId}`)
      .pipe(tap(() => this._bookings.update((list) => list.filter((b) => b.id !== bookingId))));
  }
}
