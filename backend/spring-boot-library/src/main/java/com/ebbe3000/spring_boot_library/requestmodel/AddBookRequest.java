package com.ebbe3000.spring_boot_library.requestmodel;

public record AddBookRequest(
        String title,
        String author,
        String description,
        int copies,
        String category,
        String img
) {

}
