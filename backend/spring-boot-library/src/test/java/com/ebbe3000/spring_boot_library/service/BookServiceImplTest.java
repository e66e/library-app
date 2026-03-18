package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dao.BookRepository;
import com.ebbe3000.spring_boot_library.dao.CheckoutRepository;
import com.ebbe3000.spring_boot_library.dao.HistoryRepository;
import com.ebbe3000.spring_boot_library.dao.PaymentRepository;
import com.ebbe3000.spring_boot_library.dto.BookDTO;
import com.ebbe3000.spring_boot_library.entity.Book;
import com.ebbe3000.spring_boot_library.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private CheckoutRepository checkoutRepository;
    @Mock
    private HistoryRepository historyRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private BookServiceImpl bookService;


    @Nested
    class GettingAllBooksTests {

        @Test
        void getAllBooks_happyPath_withRecords() {
            // Given
            int pageNo = 0;
            int pageSize = 20;
            Book book1 = Book.builder()
                    .id(1L)
                    .title("Test book1")
                    .author("Test author1")
                    .description("Test desc1")
                    .copies(1)
                    .copiesAvailable(1)
                    .category("TEST")
                    .img("Test image1")
                    .build();

            Book book2 = Book.builder()
                    .id(2L)
                    .title("Test book2")
                    .author("Test author2")
                    .description("Test desc2")
                    .copies(2)
                    .copiesAvailable(2)
                    .category("TEST")
                    .img("Test image2")
                    .build();
            List<Book> books = List.of(book1, book2);
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

            when(bookRepository.findAll(pageable)).thenReturn(booksPage);


            // When
            Page<BookDTO> allBooks = bookService.getAllBooks(pageNo, pageSize);

            // Then
            assertEquals(books.size(), allBooks.getTotalElements());
            assertEquals(books.stream().map(Book::mapToDTO).toList(),
                         allBooks.getContent());
            assertEquals(pageNo, allBooks.getNumber());
            assertEquals(pageSize, allBooks.getSize());
        }

        @Test
        void getBooksByNameContaining_happyPath_noRecords() {
            // Given
            int pageNo = 0;
            int pageSize = 20;
            PageRequest pageable = PageRequest.of(pageNo, pageSize);
            Page<Book> booksPage
                    = new PageImpl<>(List.of(), pageable,0);

            when(bookRepository.findAll(pageable)).thenReturn(booksPage);

            // When
            Page<BookDTO> allBooks = bookService.getAllBooks(pageNo, pageSize);


            // Then
            assertEquals(0, allBooks.getTotalElements());
            assertEquals(List.of(), allBooks.stream().toList());
            assertEquals(pageNo, allBooks.getNumber());
            assertEquals(pageSize, allBooks.getSize());
        }
    }

    @Nested
    class GettingBooksByPhraseTests {

        int pageNo;
        int pageSize;
        List<Book> books;

        @BeforeEach
        void setup() {
            this.pageNo = 0;
            this.pageSize = 20;
            Book book1 = Book.builder()
                             .id(1L)
                             .title("Test book1 extra")
                             .author("Test author1")
                             .description("Test desc1")
                             .copies(1)
                             .copiesAvailable(1)
                             .category("TEST")
                             .img("Test image1")
                             .build();

            Book book2 = Book.builder()
                             .id(2L)
                             .title("Test book2")
                             .author("Test author2")
                             .description("Test desc2")
                             .copies(2)
                             .copiesAvailable(2)
                             .category("TEST")
                             .img("Test image2")
                             .build();
            this.books = new ArrayList<>();
            this.books.addAll(List.of(book1, book2));
        }

        @Test
        void getBooksByNameContaining_allBooksMatch() {
            // Given
            String phrase = "Test";
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Book> booksPage = new PageImpl<>(this.books,  pageable, books.size());
            when(bookRepository.findByTitleContaining(phrase, pageable)).thenReturn(booksPage);

            // When
            Page<BookDTO> matchingBooks = bookService
                    .getBooksByNameContaining(phrase, this.pageNo, this.pageSize);

            // Then
            assertEquals(books.size(), matchingBooks.getTotalElements());
            assertEquals(0, matchingBooks.getNumber());
            assertEquals(20, matchingBooks.getSize());
            assertEquals(this.books.stream().map(Book::mapToDTO).toList(),
                         matchingBooks.getContent());
        }

        @Test
        void getBooksByNameContaining_oneBookMatch() {
            String phrase = "extra";
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            List<Book> booksContainingPhrase = this.books
                    .stream().filter(book -> book.getTitle().contains(phrase)).toList();
            Page<Book> booksPage = new PageImpl<>(booksContainingPhrase,
                    pageable, booksContainingPhrase.size());
            when(bookRepository.findByTitleContaining(phrase, pageable))
                    .thenReturn(booksPage);

            // When
            Page<BookDTO> matchingBooks = bookService
                    .getBooksByNameContaining(phrase, this.pageNo, this.pageSize);

            // Then
            assertEquals(1, matchingBooks.getTotalElements());
            assertEquals(0, matchingBooks.getNumber());
            assertEquals(20, matchingBooks.getSize());
            assertEquals(booksContainingPhrase.stream().map(Book::mapToDTO).toList(),
                    matchingBooks.getContent());
        }

        @Test
        void getBooksByNameContaining_noBookMatch() {
            String phrase = "not existing phrase";
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            List<Book> booksContainingPhrase = this.books
                    .stream().filter(book -> book.getTitle().contains(phrase)).toList();
            Page<Book> booksPage = new PageImpl<>(booksContainingPhrase,
                    pageable, booksContainingPhrase.size());
            when(bookRepository.findByTitleContaining(phrase, pageable))
                    .thenReturn(booksPage);

            // When
            Page<BookDTO> matchingBooks = bookService
                    .getBooksByNameContaining(phrase, this.pageNo, this.pageSize);

            // Then
            assertEquals(0, matchingBooks.getTotalElements());
            assertEquals(0, matchingBooks.getNumber());
            assertEquals(20, matchingBooks.getSize());
            assertEquals(booksContainingPhrase.stream().map(Book::mapToDTO).toList(),
                    matchingBooks.getContent());
        }
    }

    @Test
    void getBooksByCategory_test() {
        // Given
        int pageNo = 0;
        int pageSize = 20;
        Book book1 = Book.builder()
                .id(1L)
                .title("Test book1 extra")
                .author("Test author1")
                .description("Test desc1")
                .copies(1)
                .copiesAvailable(1)
                .category("TEST")
                .img("Test image1")
                .build();

        Book book2 = Book.builder()
                .id(2L)
                .title("Test book2")
                .author("Test author2")
                .description("Test desc2")
                .copies(2)
                .copiesAvailable(2)
                .category("TEST")
                .img("Test image2")
                .build();

        Book book3 = Book.builder()
                .id(3L)
                .title("Test book3")
                .author("Test author3")
                .description("Test desc3")
                .copies(2)
                .copiesAvailable(2)
                .category("NOT_TEST")
                .img("Test image2")
                .build();
        String category = "TEST";
        List<Book> books = List.of(book1, book2, book3);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Book> filteredBooksPage = new PageImpl<>(
                books.stream().filter(book -> book.getCategory().equals(category)).toList(),
                pageable, books.size()
        );
        when(bookRepository.findByCategory(category, pageable))
                .thenReturn(filteredBooksPage);

        // When
        Page<BookDTO> returnedBooks = bookService.getBooksByCategory(category, pageNo, pageSize);

        // Then
        assertEquals(filteredBooksPage.getContent().stream().map(Book::mapToDTO).toList(),
                returnedBooks.getContent());
        assertEquals(pageSize, returnedBooks.getSize());
        assertEquals(0, returnedBooks.getNumber());
        assertEquals(filteredBooksPage.getTotalElements(),
                returnedBooks.getTotalElements());
    }

    @Nested
    class GetBooksByIdTests {
        long id = 1L;
        List<Book> books;

        @BeforeEach
        void setup() {
            Book book1 = Book.builder()
                    .id(1L)
                    .title("Test book1 extra")
                    .author("Test author1")
                    .description("Test desc1")
                    .copies(1)
                    .copiesAvailable(1)
                    .category("TEST")
                    .img("Test image1")
                    .build();

            this.books = new ArrayList<>();
            this.books.add(book1);
        }

        @Test
        void getBookById_happyPath() {
            // Given
            when(bookRepository.findById(id))
                    .thenReturn(Optional.of(books
                            .stream().filter(book -> book.getId().equals(id)).findFirst().get()));

            // When
            BookDTO returnedBook = bookService.getBookById(id);

            // Then
            assertEquals(id, returnedBook.id());
            assertEquals(books.getFirst().getTitle(), returnedBook.title());
        }

        @Test
        void getBookById_throwsException() {
            // Given
            long id = 2L;
            when(bookRepository.findById(id))
                    .thenReturn(Optional.empty());

            // When
            // Then
            assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(id));
        }
    }

    @Test
    void checkoutBook_happyPath() {

    }


}