package com.innoq.calvin.booking.booking;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@GetMapping
	public List<BookingResponse> list(@RequestParam(name = "room-id", required = false) String roomId,
			@RequestParam(name = "location-id", required = false) String locationId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		if (roomId != null && date != null) {
			return bookingService.findByRoomAndDate(roomId, date);
		}
		if (locationId != null && date != null) {
			return bookingService.findByLocationAndDate(locationId, date);
		}
		return bookingService.findMyBookings();
	}

	@GetMapping("/{id}")
	public BookingResponse get(@PathVariable String id) {
		return bookingService.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookingResponse create(@Valid @RequestBody BookingRequest request) {
		return bookingService.create(request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancel(@PathVariable String id) {
		bookingService.cancel(id);
	}
}
