package com.ebbe3000.spring_boot_library.dto;

public record BookDTO(
        Long id,
        String title,
        String author,
        String description,
        int copies,
        int copiesAvailable,
        String category,
        String img) {
}
