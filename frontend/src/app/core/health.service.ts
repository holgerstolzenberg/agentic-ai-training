import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface HealthStatus {
  status: string;
  components?: Record<string, { status: string }>;
}

@Injectable({ providedIn: 'root' })
export class HealthService {
  private readonly http = inject(HttpClient);

  /** Ruft den Spring Actuator Health-Endpunkt auf. */
  check(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>('api/v1/actuator/health');
  }
}
