import { Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CatalogService } from './core/catalog.service';
import { BookingService } from './core/booking.service';
import { HealthService } from './core/health.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly catalog = inject(CatalogService);
  protected readonly booking = inject(BookingService);
  private readonly health = inject(HealthService);

  protected readonly employee = toSignal(this.catalog.currentEmployee$, { initialValue: null });
  protected readonly backendUp = signal<boolean | null>(null);

  constructor() {
    this.health.check().subscribe({
      next: (h) => this.backendUp.set(h.status === 'UP'),
      error: () => this.backendUp.set(false),
    });
  }
}
