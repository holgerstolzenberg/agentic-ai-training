package com.innoq.calvin.booking.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.innoq.calvin.booking.location.LocationRepository;
import com.innoq.calvin.booking.room.RoomRepository;
import com.innoq.calvin.booking.shared.DoubleBookingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

	@Mock
	BookingRepository bookingRepository;
	@Mock
	RoomRepository roomRepository;
	@Mock
	LocationRepository locationRepository;
	@Mock
	BookingMapper bookingMapper;

	@InjectMocks
	BookingService bookingService;

	private static final LocalDate DATE = LocalDate.of(2026, 6, 18);
	private static final String ROOM_ID = "test-room";

	// --- overlap detection via checkAvailability ---

	@Test
	void checkAvailability_returns_unavailable_for_overlapping_booking() {
		BookingEntity existing = bookingEntity("10:00", "11:00");
		when(bookingRepository.findByRoomIdAndDateAndStatus(ROOM_ID, DATE, BookingStatus.CONFIRMED))
				.thenReturn(List.of(existing));
		BookingResponse conflict = bookingResponse("10:00", "11:00");
		when(bookingMapper.toResponse(any(), any(), any())).thenReturn(conflict);

		AvailabilityResponse result = bookingService.checkAvailability(ROOM_ID, DATE, LocalTime.of(10, 30),
				LocalTime.of(12, 0));

		assertThat(result.available()).isFalse();
		assertThat(result.conflicts()).hasSize(1);
	}

	@Test
	void checkAvailability_returns_available_for_adjacent_booking() {
		BookingEntity existing = bookingEntity("09:00", "10:00");
		when(bookingRepository.findByRoomIdAndDateAndStatus(ROOM_ID, DATE, BookingStatus.CONFIRMED))
				.thenReturn(List.of(existing));

		// starts exactly when existing ends — adjacent, not overlapping
		AvailabilityResponse result = bookingService.checkAvailability(ROOM_ID, DATE, LocalTime.of(10, 0),
				LocalTime.of(11, 0));

		assertThat(result.available()).isTrue();
		assertThat(result.conflicts()).isEmpty();
	}

	@Test
	void checkAvailability_returns_available_when_no_bookings() {
		when(bookingRepository.findByRoomIdAndDateAndStatus(ROOM_ID, DATE, BookingStatus.CONFIRMED))
				.thenReturn(Collections.emptyList());

		AvailabilityResponse result = bookingService.checkAvailability(ROOM_ID, DATE, LocalTime.of(9, 0),
				LocalTime.of(10, 0));

		assertThat(result.available()).isTrue();
	}

	// --- double-booking prevention via create() ---

	@Test
	void create_throws_for_overlapping_booking() {
		BookingEntity existing = bookingEntity("10:00", "11:00");
		when(bookingRepository.findByRoomIdAndDateAndStatusForUpdate(ROOM_ID, DATE, BookingStatus.CONFIRMED))
				.thenReturn(List.of(existing));

		BookingRequest request = new BookingRequest(ROOM_ID, "loc", DATE, LocalTime.of(10, 30), LocalTime.of(12, 0),
				"Title", null);

		assertThatThrownBy(() -> bookingService.create(request)).isInstanceOf(DoubleBookingException.class);
	}

	@Test
	void create_succeeds_for_non_overlapping_booking() {
		BookingEntity existing = bookingEntity("09:00", "10:00");
		when(bookingRepository.findByRoomIdAndDateAndStatusForUpdate(ROOM_ID, DATE, BookingStatus.CONFIRMED))
				.thenReturn(List.of(existing));
		when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(roomRepository.findById(any())).thenReturn(Optional.empty());
		when(locationRepository.findById(any())).thenReturn(Optional.empty());
		when(bookingMapper.toResponse(any(), any(), any())).thenReturn(bookingResponse("10:00", "11:00"));

		BookingRequest request = new BookingRequest(ROOM_ID, "loc", DATE, LocalTime.of(10, 0), LocalTime.of(11, 0),
				"Title", null);

		assertThatCode(() -> bookingService.create(request)).doesNotThrowAnyException();
	}

	@Test
	void create_throws_for_invalid_time_window() {
		BookingRequest request = new BookingRequest(ROOM_ID, "loc", DATE, LocalTime.of(11, 0), LocalTime.of(10, 0),
				"Title", null);

		assertThatThrownBy(() -> bookingService.create(request)).isInstanceOf(IllegalArgumentException.class);
	}

	// --- helpers ---

	private BookingEntity bookingEntity(String start, String end) {
		BookingEntity b = new BookingEntity();
		b.setId("b-existing");
		b.setRoomId(ROOM_ID);
		b.setLocationId("loc");
		b.setEmployee("emp");
		b.setDate(DATE);
		b.setStartTime(LocalTime.parse(start));
		b.setEndTime(LocalTime.parse(end));
		b.setTitle("Existing");
		b.setStatus(BookingStatus.CONFIRMED);
		return b;
	}

	private BookingResponse bookingResponse(String start, String end) {
		return new BookingResponse("b-existing", ROOM_ID, "loc", "emp", DATE, LocalTime.parse(start),
				LocalTime.parse(end), "Existing", null, BookingStatus.CONFIRMED, "Room", "Location");
	}
}
