package com.ebbe3000.spring_boot_library.responsemodel;

import com.ebbe3000.spring_boot_library.dto.BookDTO;
import com.ebbe3000.spring_boot_library.entity.Book;

public record ShelfCurrentLoansResponse(
        BookDTO book,
        int daysLeft
) {

}
