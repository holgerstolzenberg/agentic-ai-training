package com.innoq.calvin.booking.shared;

public class DoubleBookingException extends RuntimeException {
	public DoubleBookingException(String message) {
		super(message);
	}
}
