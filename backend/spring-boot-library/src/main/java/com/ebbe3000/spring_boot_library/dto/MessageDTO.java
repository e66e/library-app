package com.ebbe3000.spring_boot_library.dto;

public record MessageDTO(
        Long id,
        String userEmail,
        String title,
        String question,
        String adminEmail,
        String response,
        boolean closed
) {
    public MessageDTO(String userEmail, String title, String question) {
        this(null, userEmail, title, question, null, null, false);
    }
}
