package com.ebbe3000.spring_boot_library.exception;

public class NoCredentials extends RuntimeException {
    public NoCredentials(String message) {
        super(message);
    }
}
