package com.kubsu.accounting.exception;

public class SemesterNotFoundException extends RuntimeException {

    public SemesterNotFoundException(String message) {
        super(message);
    }
}
