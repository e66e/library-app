package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dto.BookDTO;
import com.ebbe3000.spring_boot_library.responsemodel.ShelfCurrentLoansResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {

    Page<BookDTO> getAllBooks(int pageNo, int pageSize);

    Page<BookDTO> getBooksByNameContaining(String phrase, int pageNo, int pageSize);

    Page<BookDTO> getBooksByCategory(String category, int page, int size);

    BookDTO getBookById(long id);

    BookDTO checkoutBook(String userEmail, Long bookId) throws Exception;

    Boolean checkoutBookByUser(String userEmail, Long bookId);

    int currentLoansCount(String userEmail);

    List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception;

    void returnBook(String userEmail, Long bookId) throws Exception;

    void renewLoan(String userEmail, Long bookId) throws Exception;
}
