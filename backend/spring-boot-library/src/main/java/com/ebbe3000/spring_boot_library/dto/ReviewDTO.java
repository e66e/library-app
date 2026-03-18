package com.ebbe3000.spring_boot_library.dto;

import java.util.Date;

public record  ReviewDTO(
        Long Id,
        String userEmail,
        Date date,
        double rating,
        Long bookId,
        String reviewDescription
) {
}
