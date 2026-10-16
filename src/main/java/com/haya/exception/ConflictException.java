package com.haya.exception;

/** The request is valid but conflicts with the current state, e.g. a duplicate username. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
