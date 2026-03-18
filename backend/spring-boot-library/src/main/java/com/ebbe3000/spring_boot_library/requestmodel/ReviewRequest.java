package com.ebbe3000.spring_boot_library.requestmodel;

import lombok.Data;

import java.util.Optional;

public record ReviewRequest(
        double rating,
        Long bookId,
        Optional<String> reviewDescription
) {


}
