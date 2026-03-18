package com.ebbe3000.spring_boot_library.dto;

public record HistoryDTO(
        Long id,
        String userEmail,
        String checkoutDate,
        String returnedDate,
        String title,
        String author,
        String description,
        String img
) {
}
