package com.ticketbooking.common.exception;

import org.springframework.http.HttpStatus;

public class SeatConflictException extends AppException {
    public SeatConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
