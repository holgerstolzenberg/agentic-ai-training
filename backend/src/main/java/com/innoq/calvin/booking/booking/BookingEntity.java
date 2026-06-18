package com.innoq.calvin.booking.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookings", uniqueConstraints = @UniqueConstraint(name = "uq_confirmed_booking_room_date_start", columnNames = {
		"room_id", "date", "start_time", "status"}))
@Getter
@Setter
public class BookingEntity {

	@Id
	private String id;

	@Version
	private long version;

	@Column(name = "room_id", nullable = false)
	private String roomId;

	@Column(name = "location_id", nullable = false)
	private String locationId;

	@Column(nullable = false)
	private String employee;

	@Column(nullable = false)
	private LocalDate date;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Column(nullable = false)
	private String title;

	private String note;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookingStatus status;
}
