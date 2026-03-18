package com.ebbe3000.spring_boot_library.dto;

import java.math.BigDecimal;

public record PaymentDTO(
        Long id,
        String userEmail,
        BigDecimal amount
) {
}
