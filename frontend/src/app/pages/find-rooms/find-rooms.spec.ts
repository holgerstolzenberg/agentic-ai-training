import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Subject } from 'rxjs';
import { FindRooms } from './find-rooms';
import { CatalogService } from '../../core/catalog.service';
import { Employee } from '../../core/models';

describe('FindRooms', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FindRooms],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should create the component', () => {
    const fixture = TestBed.createComponent(FindRooms);
    expect(fixture.componentInstance).toBeTruthy();
    httpTesting.match(() => true); // absorb any pending requests
  });

  it('sets locationId from employee home location when no query param is present', () => {
    const fixture = TestBed.createComponent(FindRooms);
    const component = fixture.componentInstance;

    const req = httpTesting.expectOne('api/v1/employees/current');
    req.flush({ id: 'emp1', name: 'Test User', role: 'dev', initials: 'TU', homeLocationId: 'ber' } satisfies Employee);

    expect(component['locationId']()).toBe('ber');
    httpTesting.match(() => true);
  });

  it('does not update locationId after component is destroyed (no subscription leak)', () => {
    const employeeSubject = new Subject<Employee>();
    const catalog = TestBed.inject(CatalogService);
    Object.defineProperty(catalog, 'currentEmployee$', { value: employeeSubject.asObservable(), writable: true });

    const fixture = TestBed.createComponent(FindRooms);
    const component = fixture.componentInstance;
    const initialLocation = component['locationId']();

    fixture.destroy();

    // Emitting after destroy must not change the signal
    employeeSubject.next({ id: 'emp1', name: 'Test User', role: 'dev', initials: 'TU', homeLocationId: 'muc' });
    expect(component['locationId']()).toBe(initialLocation);

    httpTesting.match(() => true);
  });
});
