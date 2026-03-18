package com.ebbe3000.spring_boot_library.exception;

public class OutstandingFees extends RuntimeException {
    public OutstandingFees(String message) {
        super(message);
    }
}
