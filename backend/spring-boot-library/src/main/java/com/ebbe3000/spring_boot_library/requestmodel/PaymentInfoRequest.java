package com.ebbe3000.spring_boot_library.requestmodel;

public record PaymentInfoRequest(
        int amount,
        String currency,
        String receiptEmail
) {
}
