package com.rocketseat.pass_in.domain.attendee.exceptions;

public class AttendeeAlreadyExistsException extends RuntimeException {
    public AttendeeAlreadyExistsException(String message) {
        super(message);
    }
}
