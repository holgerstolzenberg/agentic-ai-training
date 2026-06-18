package com.innoq.calvin.booking.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

	private final BookingService bookingService;

	public AvailabilityController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@GetMapping
	public AvailabilityResponse check(@RequestParam(name = "room-id") String roomId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam(name = "start-time") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
			@RequestParam(name = "end-time") @DateTimeFormat(pattern = "HH:mm") LocalTime endTime) {
		return bookingService.checkAvailability(roomId, date, startTime, endTime);
	}
}
