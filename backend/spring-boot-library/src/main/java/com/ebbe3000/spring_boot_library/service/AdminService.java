package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.requestmodel.AddBookRequest;

public interface AdminService {

    void postBook(AddBookRequest addBookRequest);

    void increaseBookQuantity(Long bookId);

    void decreaseBookQuantity(Long bookId);

    void deleteBook(Long bookId);
}
