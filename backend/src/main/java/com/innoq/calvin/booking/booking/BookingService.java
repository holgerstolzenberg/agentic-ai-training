package com.innoq.calvin.booking.booking;

import com.innoq.calvin.booking.location.LocationEntity;
import com.innoq.calvin.booking.location.LocationRepository;
import com.innoq.calvin.booking.room.RoomEntity;
import com.innoq.calvin.booking.room.RoomRepository;
import com.innoq.calvin.booking.shared.DoubleBookingException;
import com.innoq.calvin.booking.shared.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

	// TODO(TS-1): replace with authenticated principal once Basic Auth is
	// integrated
	private static final String CURRENT_EMPLOYEE = "alex-berger";

	private final BookingRepository bookingRepository;
	private final RoomRepository roomRepository;
	private final LocationRepository locationRepository;
	private final BookingMapper bookingMapper;

	public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository,
			LocationRepository locationRepository, BookingMapper bookingMapper) {
		this.bookingRepository = bookingRepository;
		this.roomRepository = roomRepository;
		this.locationRepository = locationRepository;
		this.bookingMapper = bookingMapper;
	}

	public List<BookingResponse> findMyBookings() {
		List<BookingEntity> bookings = bookingRepository
				.findByEmployeeAndStatusOrderByDateAscStartTimeAsc(CURRENT_EMPLOYEE, BookingStatus.CONFIRMED);
		return toResponseList(bookings);
	}

	public List<BookingResponse> findByRoomAndDate(String roomId, LocalDate date) {
		List<BookingEntity> bookings = bookingRepository.findByRoomIdAndDateAndStatusOrderByStartTimeAsc(roomId, date,
				BookingStatus.CONFIRMED);
		return toResponseList(bookings);
	}

	public List<BookingResponse> findByLocationAndDate(String locationId, LocalDate date) {
		List<BookingEntity> bookings = bookingRepository.findByLocationIdAndDateAndStatus(locationId, date,
				BookingStatus.CONFIRMED);
		return toResponseList(bookings);
	}

	public BookingResponse findById(String id) {
		return bookingRepository.findById(id).map(this::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
	}

	public AvailabilityResponse checkAvailability(String roomId, LocalDate date, LocalTime startTime,
			LocalTime endTime) {
		List<BookingEntity> dayBookings = bookingRepository.findByRoomIdAndDateAndStatus(roomId, date,
				BookingStatus.CONFIRMED);
		List<BookingResponse> conflicts = toResponseList(dayBookings.stream()
				.filter(b -> overlaps(b.getStartTime(), b.getEndTime(), startTime, endTime)).toList());
		return new AvailabilityResponse(conflicts.isEmpty(), conflicts);
	}

	@Transactional
	public BookingResponse create(BookingRequest request) {
		if (!request.endTime().isAfter(request.startTime())) {
			throw new IllegalArgumentException("End time must be after start time");
		}

		List<BookingEntity> dayBookings = bookingRepository.findByRoomIdAndDateAndStatusForUpdate(request.roomId(),
				request.date(), BookingStatus.CONFIRMED);
		boolean hasConflict = dayBookings.stream()
				.anyMatch(b -> overlaps(b.getStartTime(), b.getEndTime(), request.startTime(), request.endTime()));
		if (hasConflict) {
			throw new DoubleBookingException("Room " + request.roomId() + " is already booked for that time window");
		}

		BookingEntity entity = new BookingEntity();
		entity.setId("b-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
		entity.setRoomId(request.roomId());
		entity.setLocationId(request.locationId());
		entity.setEmployee(CURRENT_EMPLOYEE);
		entity.setDate(request.date());
		entity.setStartTime(request.startTime());
		entity.setEndTime(request.endTime());
		entity.setTitle(request.title());
		entity.setNote(request.note());
		entity.setStatus(BookingStatus.CONFIRMED);

		try {
			return toResponse(bookingRepository.save(entity));
		} catch (DataIntegrityViolationException e) {
			throw new DoubleBookingException("Room was booked concurrently — please try again");
		}
	}

	@Transactional
	public void cancel(String id) {
		BookingEntity entity = bookingRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
		entity.setStatus(BookingStatus.CANCELLED);
		bookingRepository.save(entity);
	}

	private List<BookingResponse> toResponseList(List<BookingEntity> bookings) {
		Set<String> roomIds = bookings.stream().map(BookingEntity::getRoomId).collect(Collectors.toSet());
		Set<String> locationIds = bookings.stream().map(BookingEntity::getLocationId).collect(Collectors.toSet());
		Map<String, String> roomNames = roomRepository.findAllById(roomIds).stream()
				.collect(Collectors.toMap(RoomEntity::getId, RoomEntity::getName));
		Map<String, String> locationNames = locationRepository.findAllById(locationIds).stream()
				.collect(Collectors.toMap(LocationEntity::getId, LocationEntity::getName));
		return bookings.stream()
				.map(b -> bookingMapper.toResponse(b, roomNames.getOrDefault(b.getRoomId(), b.getRoomId()),
						locationNames.getOrDefault(b.getLocationId(), b.getLocationId())))
				.toList();
	}

	private BookingResponse toResponse(BookingEntity e) {
		String roomName = roomRepository.findById(e.getRoomId()).map(r -> r.getName()).orElse(e.getRoomId());
		String locationName = locationRepository.findById(e.getLocationId()).map(l -> l.getName())
				.orElse(e.getLocationId());
		return bookingMapper.toResponse(e, roomName, locationName);
	}

	private boolean overlaps(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
		return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
	}
}
