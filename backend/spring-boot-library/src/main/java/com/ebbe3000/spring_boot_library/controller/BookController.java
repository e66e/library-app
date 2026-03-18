package com.ebbe3000.spring_boot_library.controller;

import com.ebbe3000.spring_boot_library.dto.BookDTO;
import com.ebbe3000.spring_boot_library.exception.NoCredentials;
import com.ebbe3000.spring_boot_library.responsemodel.ShelfCurrentLoansResponse;
import com.ebbe3000.spring_boot_library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin
public class BookController {
    private final BookService bookService;

    @GetMapping()
    public PagedModel<BookDTO> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "20", required = false) int size
    ) {
        Page<BookDTO> allBooks = this.bookService.getAllBooks(page, size);

        return new PagedModel<>(allBooks);
    }

    @GetMapping(path = "/search/findByTitleContaining", params = "title")
    public PagedModel<BookDTO> findBooksByTitleContaining(
            @RequestParam(name = "title", defaultValue = "") String title,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "20", required = false)
            int size
    ) {
        Page<BookDTO> pageContainingTitle = this.bookService.getBooksByNameContaining(title, page, size);

        return new PagedModel<>(pageContainingTitle);
    }

    @GetMapping(params = "category")
    public PagedModel<BookDTO> findBooksByCategory(
            @RequestParam(name = "category", defaultValue = "%") String category,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "20", required = false) int size
    ) {
        Page<BookDTO> pageContainingTitle = this.bookService.getBooksByCategory(category, page, size);

        return new PagedModel<>(pageContainingTitle);
    }

    @GetMapping(value="/{id}")
    public BookDTO findBookById(@PathVariable(value = "id") long id) {
        return this.bookService.getBookById(id);
    }

    @GetMapping("/secure/currentloans")
    public List<ShelfCurrentLoansResponse> currentLoans(JwtAuthenticationToken jwtAuthenticationToken) throws Exception {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("User email is missing.");
        }
        return this.bookService.currentLoans(userEmail);
    }

    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount(JwtAuthenticationToken jwtAuthenticationToken) throws Exception {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("User email is missing.");
        }
        return this.bookService
                .currentLoansCount(userEmail);
    }

    @GetMapping("/secure/ischeckedout/byuser")
    public Boolean checkoutBookByUser(JwtAuthenticationToken jwtAuthenticationToken,
                                      @RequestParam Long bookId) throws Exception {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("User email is missing.");
        }
        return this.bookService
                .checkoutBookByUser(userEmail, bookId);
    }

    @PutMapping("/secure/checkout")
    public BookDTO checkoutBook(JwtAuthenticationToken jwtAuthenticationToken,
                                @RequestParam Long bookId) throws Exception {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("User email is missing.");
        }
        return this.bookService
                .checkoutBook(userEmail, bookId);
    }

    @PutMapping("/secure/return")
    public void returnBook(JwtAuthenticationToken jwtAuthenticationToken,
                           @RequestParam Long bookId) throws Exception {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("User email is missing.");
        }
        this.bookService.returnBook(userEmail, bookId);
    }

    @PutMapping("/secure/renew/loan")
    public void renewLoan(JwtAuthenticationToken jwtAuthenticationToken,
                          @RequestParam Long bookId) throws Exception {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new NoCredentials("User email is missing.");
        }
        this.bookService.renewLoan(userEmail, bookId);
    }
}
