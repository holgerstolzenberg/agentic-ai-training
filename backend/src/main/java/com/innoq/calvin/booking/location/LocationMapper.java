package com.innoq.calvin.booking.location;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

	LocationResponse toResponse(LocationEntity entity, long roomCount);
}
