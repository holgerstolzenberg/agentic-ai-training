import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { Equipment, ConferenceRoom, Employee, Location } from './models';

/**
 * Provides master data (locations, conference rooms, equipment, employees)
 * via HTTP from the Booking Service backend.
 */
@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly http = inject(HttpClient);

  private readonly locations$ = this.http.get<Location[]>('api/v1/locations').pipe(shareReplay(1));
  private readonly equipment$ = this.http.get<Equipment[]>('api/v1/equipment').pipe(shareReplay(1));

  readonly currentEmployee$ = this.http.get<Employee>('api/v1/employees/current').pipe(shareReplay(1));

  getLocations(): Observable<Location[]> {
    return this.locations$;
  }

  getLocation(id: string): Observable<Location | undefined> {
    return this.locations$.pipe(map((locs) => locs.find((l) => l.id === id)));
  }

  getEquipment(): Observable<Equipment[]> {
    return this.equipment$;
  }

  getEquipmentById(id: string): Observable<Equipment | undefined> {
    return this.equipment$.pipe(map((items) => items.find((e) => e.id === id)));
  }

  getRooms(locationId?: string): Observable<ConferenceRoom[]> {
    const params: Record<string, string> = locationId ? { 'location-id': locationId } : {};
    return this.http.get<ConferenceRoom[]>('api/v1/rooms', { params });
  }

  getRoom(id: string): Observable<ConferenceRoom> {
    return this.http.get<ConferenceRoom>(`api/v1/rooms/${id}`);
  }
}
