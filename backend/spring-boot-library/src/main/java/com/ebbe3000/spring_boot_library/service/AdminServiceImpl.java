package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dao.BookRepository;
import com.ebbe3000.spring_boot_library.dao.CheckoutRepository;
import com.ebbe3000.spring_boot_library.dao.ReviewRepository;
import com.ebbe3000.spring_boot_library.entity.Book;
import com.ebbe3000.spring_boot_library.exception.ResourceNotFoundException;
import com.ebbe3000.spring_boot_library.requestmodel.AddBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final CheckoutRepository checkoutRepository;

    @Override
    @Transactional
    public void postBook(AddBookRequest addBookRequest) {
        Book book = new Book();
        book.setTitle(addBookRequest.title());
        book.setAuthor(addBookRequest.author());
        book.setDescription(addBookRequest.description());
        book.setCopies(addBookRequest.copies());
        book.setCopiesAvailable(addBookRequest.copies());
        book.setCategory(addBookRequest.category());
        book.setImg(addBookRequest.img());
        this.bookRepository.save(book);
    }

    @Override
    @Transactional
    public void increaseBookQuantity(Long bookId) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new ResourceNotFoundException("Book was not found.");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        book.get().setCopies(book.get().getCopies() + 1);

        this.bookRepository.save(book.get());
    }

    @Override
    public void decreaseBookQuantity(Long bookId) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        if (book.isEmpty() || book.get().getCopiesAvailable() <= 0 || book.get().getCopies() <= 0) {
            throw new ResourceNotFoundException("Book was not found or quantity locked.");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        book.get().setCopies(book.get().getCopies() - 1);

        this.bookRepository.save(book.get());
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        Optional<Book> book = this.bookRepository.findById(bookId);

        if (book.isEmpty()) {
            throw new ResourceNotFoundException("Book was not found.");
        }

        this.bookRepository.delete(book.get());
        this.checkoutRepository.deleteAllByBookId(bookId);
        this.reviewRepository.deleteAllByBookId(bookId);
    }
}
