package com.innoq.calvin.booking.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BookingRepositoryTest {

	@Autowired
	BookingRepository bookingRepository;

	private static final LocalDate DATE = LocalDate.of(2026, 6, 18);
	private static final String ROOM_ID = "test-room-repo";

	@Test
	void saves_and_retrieves_a_confirmed_booking() {
		bookingRepository.save(booking("b-t1", "09:00", "10:00", BookingStatus.CONFIRMED));

		List<BookingEntity> found = bookingRepository.findByRoomIdAndDateAndStatus(ROOM_ID, DATE,
				BookingStatus.CONFIRMED);

		assertThat(found).hasSize(1);
		assertThat(found.get(0).getId()).isEqualTo("b-t1");
	}

	@Test
	void pessimistic_lock_query_returns_same_results_as_plain_query() {
		bookingRepository.save(booking("b-t2", "10:00", "11:00", BookingStatus.CONFIRMED));
		bookingRepository.save(booking("b-t3", "12:00", "13:00", BookingStatus.CANCELLED));

		List<BookingEntity> locked = bookingRepository.findByRoomIdAndDateAndStatusForUpdate(ROOM_ID, DATE,
				BookingStatus.CONFIRMED);
		List<BookingEntity> plain = bookingRepository.findByRoomIdAndDateAndStatus(ROOM_ID, DATE,
				BookingStatus.CONFIRMED);

		assertThat(locked).extracting(BookingEntity::getId)
				.containsExactlyInAnyOrderElementsOf(plain.stream().map(BookingEntity::getId).toList());
	}

	@Test
	void cancelled_bookings_are_excluded_from_confirmed_query() {
		bookingRepository.save(booking("b-t4", "09:00", "10:00", BookingStatus.CANCELLED));

		List<BookingEntity> found = bookingRepository.findByRoomIdAndDateAndStatus(ROOM_ID, DATE,
				BookingStatus.CONFIRMED);

		assertThat(found).isEmpty();
	}

	private BookingEntity booking(String id, String start, String end, BookingStatus status) {
		BookingEntity b = new BookingEntity();
		b.setId(id);
		b.setRoomId(ROOM_ID);
		b.setLocationId("loc-1");
		b.setEmployee("emp-1");
		b.setDate(DATE);
		b.setStartTime(LocalTime.parse(start));
		b.setEndTime(LocalTime.parse(end));
		b.setTitle("Test Meeting");
		b.setStatus(status);
		return b;
	}
}
