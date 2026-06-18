package com.innoq.calvin.booking.booking;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<BookingEntity, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM BookingEntity b WHERE b.roomId = :roomId AND b.date = :date AND b.status = :status")
	List<BookingEntity> findByRoomIdAndDateAndStatusForUpdate(@Param("roomId") String roomId,
			@Param("date") LocalDate date, @Param("status") BookingStatus status);

	List<BookingEntity> findByRoomIdAndDateAndStatus(String roomId, LocalDate date, BookingStatus status);

	List<BookingEntity> findByRoomIdAndDateAndStatusOrderByStartTimeAsc(String roomId, LocalDate date,
			BookingStatus status);

	List<BookingEntity> findByLocationIdAndDateAndStatus(String locationId, LocalDate date, BookingStatus status);

	List<BookingEntity> findByEmployeeAndStatusOrderByDateAscStartTimeAsc(String employee, BookingStatus status);
}
