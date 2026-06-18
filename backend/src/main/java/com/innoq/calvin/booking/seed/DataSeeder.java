package com.innoq.calvin.booking.seed;

import com.innoq.calvin.booking.booking.BookingEntity;
import com.innoq.calvin.booking.booking.BookingRepository;
import com.innoq.calvin.booking.booking.BookingStatus;
import com.innoq.calvin.booking.employee.EmployeeEntity;
import com.innoq.calvin.booking.employee.EmployeeRepository;
import com.innoq.calvin.booking.equipment.EquipmentEntity;
import com.innoq.calvin.booking.equipment.EquipmentRepository;
import com.innoq.calvin.booking.location.LocationEntity;
import com.innoq.calvin.booking.location.LocationRepository;
import com.innoq.calvin.booking.room.RoomEntity;
import com.innoq.calvin.booking.room.RoomRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

	private static final String[] ROOM_COLORS = {"#004153", "#ff9c66", "#009999", "#ff4d67", "#fff019", "#005268",
			"#68ddc3", "#d01040"};

	// IDs intentionally match the German-language equipment entities seeded below
	private static final String[] EQUIPMENT_POOL = {"bildschirm", "beamer", "whiteboard", "flipchart", "videokonferenz",
			"telefonkonferenz", "klimaanlage", "hdmi", "barrierefrei", "kaffee"};

	private static final Map<String, String[]> ROOM_THEMES = Map.of("mh",
			new String[]{"Turing", "Lovelace", "Hopper", "Knuth", "Dijkstra"}, "be",
			new String[]{"Neumann", "Kay", "Ritchie", "Liskov"}, "hh", new String[]{"Torvalds", "Wirth", "Backus"}, "k",
			new String[]{"Hamilton", "Engelbart", "Cerf", "Berners-Lee"}, "m",
			new String[]{"Shannon", "Hoare", "Allen"}, "zh", new String[]{"Wirth", "Nygaard", "Iverson"}, "baar",
			new String[]{"Floyd", "Milner"}, "of", new String[]{"Goldberg", "Sutherland", "Codd"});

	// Location order must match the TypeScript LOCATIONS array order for
	// deterministic room IDs and equipment assignment.
	private static final List<String> LOCATION_ORDER = List.of("mh", "be", "hh", "k", "m", "zh", "baar", "of");

	private final LocationRepository locationRepository;
	private final EquipmentRepository equipmentRepository;
	private final RoomRepository roomRepository;
	private final EmployeeRepository employeeRepository;
	private final BookingRepository bookingRepository;

	public DataSeeder(LocationRepository locationRepository, EquipmentRepository equipmentRepository,
			RoomRepository roomRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository) {
		this.locationRepository = locationRepository;
		this.equipmentRepository = equipmentRepository;
		this.roomRepository = roomRepository;
		this.employeeRepository = employeeRepository;
		this.bookingRepository = bookingRepository;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (locationRepository.count() > 0)
			return;
		seedLocations();
		seedEquipment();
		seedRooms();
		seedEmployees();
		seedBookings();
	}

	private void seedLocations() {
		locationRepository.saveAll(List.of(
				location("mh", "Monheim", "Monheim am Rhein", "Germany", "MH",
						"Krischerstraße 100, 40789 Monheim am Rhein", true),
				location("be", "Berlin", "Berlin", "Germany", "BE", "Ohlauer Straße 43, 10999 Berlin", false),
				location("hh", "Hamburg", "Hamburg", "Germany", "HH", "Ludwig-Erhard-Straße 18, 20459 Hamburg", false),
				location("k", "Köln", "Köln", "Germany", "K", "Im Mediapark 5, 50670 Köln", false),
				location("m", "München", "München", "Germany", "M", "Sonnenstraße 19, 80331 München", false),
				location("zh", "Zürich", "Zürich", "Switzerland", "ZH", "Förrlibuckstrasse 110, 8005 Zürich", false),
				location("baar", "Baar", "Baar", "Switzerland", "BA", "Lindenstrasse 16, 6340 Baar", false),
				location("of", "Offenbach", "Offenbach am Main", "Germany", "OF",
						"Kaiserstraße 39, 63065 Offenbach am Main", false)));
	}

	private void seedEquipment() {
		equipmentRepository.saveAll(List.of(equipment("bildschirm", "Screen", "🖥️"),
				equipment("beamer", "Projector", "📽️"), equipment("whiteboard", "Whiteboard", "🧑‍🏫"),
				equipment("flipchart", "Flipchart", "📋"), equipment("videokonferenz", "Video conferencing", "📹"),
				equipment("telefonkonferenz", "Phone conferencing", "☎️"),
				equipment("klimaanlage", "Air conditioning", "❄️"), equipment("hdmi", "HDMI port", "🔌"),
				equipment("barrierefrei", "Accessible", "♿"), equipment("kaffee", "Coffee machine", "☕")));
	}

	private void seedRooms() {
		int[] capacities = {4, 6, 8, 10, 12, 16, 20};
		List<RoomEntity> rooms = new ArrayList<>();
		int count = 0;

		for (String locationId : LOCATION_ORDER) {
			LocationEntity loc = locationRepository.findById(locationId).orElseThrow();
			String[] names = ROOM_THEMES.getOrDefault(locationId, new String[]{"Alpha", "Beta"});

			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				int capacity = capacities[(count + i) % capacities.length];
				int featureCount = 3 + ((count + i) % 5);

				List<String> equipmentIds = new ArrayList<>();
				for (int m = 0; m < featureCount; m++) {
					String eqId = EQUIPMENT_POOL[(count * 3 + i * 2 + m * 5) % EQUIPMENT_POOL.length];
					if (!equipmentIds.contains(eqId))
						equipmentIds.add(eqId);
				}

				int floor = (i % 4) + 1;
				String roomId = locationId + "-" + name.toLowerCase().replaceAll("[^a-z]", "");

				RoomEntity room = new RoomEntity();
				room.setId(roomId);
				room.setName(name);
				room.setLocationId(locationId);
				room.setCapacity(capacity);
				room.setFloorInfo("Floor " + floor + " · " + loc.getCity());
				room.setAreaSqm((int) Math.round(capacity * 2.2 + 6));
				room.setDescription("Conference room \"" + name + "\" at " + loc.getName() + " – suitable for up to "
						+ capacity + " people.");
				room.setEquipmentIds(equipmentIds);
				room.setColor(ROOM_COLORS[count % ROOM_COLORS.length]);
				rooms.add(room);
				count++;
			}
		}
		roomRepository.saveAll(rooms);
	}

	private void seedEmployees() {
		EmployeeEntity alex = new EmployeeEntity();
		alex.setId("alex-berger");
		alex.setName("Alex Berger");
		alex.setRole("Senior Consultant");
		alex.setHomeLocationId("k");
		alex.setInitials("AB");
		employeeRepository.save(alex);
	}

	private void seedBookings() {
		LocalDate today = LocalDate.now();
		bookingRepository.saveAll(List.of(
				booking("b-1001", "k-hamilton", "k", "mara-lindqvist", today, "09:00", "10:30", "Sprint Planning",
						null),
				booking("b-1002", "k-hamilton", "k", "jens-brandt", today, "13:00", "14:00", "Architecture Review",
						null),
				booking("b-1003", "k-engelbart", "k", "alex-berger", today, "11:00", "12:00",
						"Client Workshop Preparation", "Projector needed"),
				booking("b-1004", "k-cerf", "k", "alex-berger", today.plusDays(2), "14:00", "16:00",
						"Team Retrospective", null),
				booking("b-1005", "be-neumann", "be", "alex-berger", today.plusDays(5), "10:00", "11:30",
						"Client Meeting Acme AG", "VC with Stuttgart"),
				booking("b-1006", "mh-turing", "mh", "sophie-krause", today.plusDays(1), "09:30", "11:00", "Onboarding",
						null),
				booking("b-1007", "mh-lovelace", "mh", "tom-vogel", today.plusDays(1), "15:00", "17:00",
						"Design Sprint", null),
				booking("b-1008", "hh-torvalds", "hh", "nina-hofmann", today.plusDays(3), "13:30", "15:00",
						"Pairing Session", null)));
	}

	private LocationEntity location(String id, String name, String city, String country, String abbreviation,
			String address, boolean headquarters) {
		LocationEntity e = new LocationEntity();
		e.setId(id);
		e.setName(name);
		e.setCity(city);
		e.setCountry(country);
		e.setAbbreviation(abbreviation);
		e.setAddress(address);
		e.setHeadquarters(headquarters);
		return e;
	}

	private EquipmentEntity equipment(String id, String name, String icon) {
		EquipmentEntity e = new EquipmentEntity();
		e.setId(id);
		e.setName(name);
		e.setIcon(icon);
		return e;
	}

	private BookingEntity booking(String id, String roomId, String locationId, String employee, LocalDate date,
			String startTime, String endTime, String title, String note) {
		BookingEntity b = new BookingEntity();
		b.setId(id);
		b.setRoomId(roomId);
		b.setLocationId(locationId);
		b.setEmployee(employee);
		b.setDate(date);
		b.setStartTime(LocalTime.parse(startTime));
		b.setEndTime(LocalTime.parse(endTime));
		b.setTitle(title);
		b.setNote(note);
		b.setStatus(BookingStatus.CONFIRMED);
		return b;
	}
}
