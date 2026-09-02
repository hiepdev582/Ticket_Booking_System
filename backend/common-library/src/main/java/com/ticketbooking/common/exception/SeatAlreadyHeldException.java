package com.ticketbooking.common.exception;

import org.springframework.http.HttpStatus;

public class SeatAlreadyHeldException extends AppException {
    public SeatAlreadyHeldException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
