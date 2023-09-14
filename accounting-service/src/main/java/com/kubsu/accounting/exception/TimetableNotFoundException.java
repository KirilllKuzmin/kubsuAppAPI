package com.kubsu.accounting.exception;

public class TimetableNotFoundException extends RuntimeException {

    public TimetableNotFoundException(String message) {
        super(message);
    }
}
