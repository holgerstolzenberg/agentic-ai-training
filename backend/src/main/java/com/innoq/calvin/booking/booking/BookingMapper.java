package com.innoq.calvin.booking.booking;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

	BookingResponse toResponse(BookingEntity entity, String roomName, String locationName);
}
