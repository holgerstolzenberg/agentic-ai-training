package com.innoq.calvin.booking.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequest(@NotBlank String roomId, @NotBlank String locationId, @NotNull LocalDate date,
		@NotNull @JsonFormat(pattern = "HH:mm") LocalTime startTime,
		@NotNull @JsonFormat(pattern = "HH:mm") LocalTime endTime, @NotBlank String title, String note) {
}
