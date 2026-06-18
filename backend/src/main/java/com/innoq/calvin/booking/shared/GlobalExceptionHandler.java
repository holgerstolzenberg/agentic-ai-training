package com.innoq.calvin.booking.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	ProblemDetail notFound(ResourceNotFoundException ex) {
		var p = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
		p.setTitle("Not Found");
		p.setDetail(ex.getMessage());
		return p;
	}

	@ExceptionHandler(DoubleBookingException.class)
	ProblemDetail conflict(DoubleBookingException ex) {
		var p = ProblemDetail.forStatus(HttpStatus.CONFLICT);
		p.setTitle("Booking Conflict");
		p.setDetail(ex.getMessage());
		return p;
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ProblemDetail badRequest(IllegalArgumentException ex) {
		var p = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		p.setTitle("Bad Request");
		p.setDetail(ex.getMessage());
		return p;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ProblemDetail validationError(MethodArgumentNotValidException ex) {
		var p = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		p.setTitle("Validation Error");
		p.setDetail(
				ex.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage())
						.reduce((a, b) -> a + "; " + b).orElse("Invalid request"));
		return p;
	}
}
