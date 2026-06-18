/**
 * Domain model for Calvin.
 *
 * The terms follow the Ubiquitous Language from
 * `docs/product/glossary.md`. Each entity is its own interface;
 * relationships are expressed via IDs.
 */

/** Countries in which INNOQ operates office locations. */
export type Country = 'Germany' | 'Switzerland';

/**
 * Location – one of the eight INNOQ office locations.
 * (Glossary: "Standort")
 */
export interface Location {
  id: string;
  name: string;
  city: string;
  country: Country;
  /** Short abbreviation, e.g. "MH" for Monheim. */
  abbreviation: string;
  address: string;
  /** Is this the headquarters? */
  headquarters?: boolean;
  /** Number of conference rooms at this location (provided by backend). */
  roomCount?: number;
}

/**
 * Equipment – a technical or spatial feature of a conference room.
 * (Glossary: "Ausstattung")
 */
export interface Equipment {
  id: string;
  name: string;
  /** Emoji as a lightweight icon for the prototype. */
  icon: string;
}

/**
 * ConferenceRoom – the central bookable resource in Calvin.
 * (Glossary: "Konferenzraum")
 */
export interface ConferenceRoom {
  id: string;
  name: string;
  locationId: string;
  /** Maximum number of participants. */
  capacity: number;
  /** Position of the room at the location, e.g. "2nd floor, North wing". */
  floorInfo: string;
  areaSqm: number;
  description: string;
  /** IDs of the available equipment features. */
  equipmentIds: string[];
  /** Accent color for the visual representation in the prototype. */
  color: string;
}

/** Status of a room booking. */
export type BookingStatus = 'confirmed' | 'cancelled';

/**
 * RoomBooking – a reservation of a conference room for a
 * time period and purpose. (Glossary: "Raumbuchung")
 */
export interface RoomBooking {
  id: string;
  roomId: string;
  locationId: string;
  /** Name of the booking employee. */
  employee: string;
  /** Date in ISO format YYYY-MM-DD. */
  date: string;
  /** Start time in HH:mm format. */
  startTime: string;
  /** End time in HH:mm format. */
  endTime: string;
  title: string;
  note?: string;
  status: BookingStatus;
  /** Denormalized room name (provided by backend). */
  roomName?: string;
  /** Denormalized location name (provided by backend). */
  locationName?: string;
}

/**
 * Employee – the primary target group of Calvin.
 * (Glossary: "INNOQ-Mitarbeiter")
 */
export interface Employee {
  id: string;
  name: string;
  role: string;
  /** Home location of the employee. */
  homeLocationId: string;
  initials: string;
}

/**
 * Availability result for a room within a specific time window.
 * (Glossary: "Verfügbarkeit" / "Verfügbarkeitsanzeige")
 */
export interface Availability {
  available: boolean;
  /** Bookings that conflict with the requested time window. */
  conflicts: RoomBooking[];
}
