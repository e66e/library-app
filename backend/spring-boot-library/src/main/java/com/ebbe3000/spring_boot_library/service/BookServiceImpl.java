package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dao.BookRepository;
import com.ebbe3000.spring_boot_library.dao.CheckoutRepository;
import com.ebbe3000.spring_boot_library.dao.HistoryRepository;
import com.ebbe3000.spring_boot_library.dao.PaymentRepository;
import com.ebbe3000.spring_boot_library.dto.BookDTO;
import com.ebbe3000.spring_boot_library.entity.Book;
import com.ebbe3000.spring_boot_library.entity.Checkout;
import com.ebbe3000.spring_boot_library.entity.History;
import com.ebbe3000.spring_boot_library.entity.Payment;
import com.ebbe3000.spring_boot_library.exception.OutstandingFees;
import com.ebbe3000.spring_boot_library.exception.ResourceNotFoundException;
import com.ebbe3000.spring_boot_library.responsemodel.ShelfCurrentLoansResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CheckoutRepository checkoutRepository;
    private final HistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public Page<BookDTO> getAllBooks(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Book> allBooks = this.bookRepository.findAll(pageable);

        return new PageImpl<>(allBooks.stream().map(Book::mapToDTO).toList(),
                              allBooks.getPageable(),
                              allBooks.getTotalElements());
    }

    @Override
    public Page<BookDTO> getBooksByNameContaining(String phrase, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Book> books = this.bookRepository.findByTitleContaining(phrase, pageable);

        return new PageImpl<>(books.stream().map(Book::mapToDTO).toList(),
                              books.getPageable(),
                              books.getTotalElements());
    }

    @Override
    public Page<BookDTO> getBooksByCategory(String category, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Book> books = this.bookRepository.findByCategory(category, pageable);

        return new PageImpl<>(books.stream().map(Book::mapToDTO).toList(),
                              books.getPageable(),
                              books.getTotalElements());
    }

    @Override
    public BookDTO getBookById(long id) {
        Book book = this.bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found."));
        return book.mapToDTO();
    }

    @Override
    @Transactional
    public BookDTO checkoutBook(String userEmail, Long bookId) throws Exception {
        Optional<Book> book = this.bookRepository.findById(bookId);

        Checkout validateCheckout = this.checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (book.isEmpty() || validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book doesn't exists or already checked out by a user!");
        }

        List<Checkout> currentBookCheckout = this.checkoutRepository.findBooksByUserEmail(userEmail);

        boolean bookNeedsReturned = false;

        for (Checkout checkout : currentBookCheckout) {
            LocalDate date1 = LocalDate.parse(checkout.getReturnDate());
            LocalDate date2 = LocalDate.now();

            long differenceInTime = ChronoUnit.DAYS.between(date2, date1);

            if (differenceInTime < 0) {
                bookNeedsReturned = true;
                break;
            }
        }

        Optional<Payment> userPayment = this.paymentRepository.findByUserEmail(userEmail);

        if ((userPayment.isPresent() && userPayment.get().getAmount().compareTo(BigDecimal.ZERO) > 0) ||
            (userPayment.isPresent() && bookNeedsReturned)) {
            throw new OutstandingFees("Outstanding fees");
        }

        if (userPayment.isEmpty()) {
            Payment payment = new Payment();
            payment.setAmount(BigDecimal.ZERO);
            payment.setUserEmail(userEmail);
            this.paymentRepository.save(payment);
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        this.bookRepository.save(book.get());

        Checkout newCheckout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                bookId);

        this.checkoutRepository.save(newCheckout);

        return book.get().mapToDTO();
    }

    @Override
    public Boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout validateCheckout = this.checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        return validateCheckout != null;
    }

    @Override
    public int currentLoansCount(String userEmail) {
        return this.checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    @Override
    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {
        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();

        List<Checkout> checkoutList = this.checkoutRepository.findBooksByUserEmail(userEmail);
        List<Long> bookIdList = checkoutList.stream().map(Checkout::getBookId).toList();

        List<Book> books = this.bookRepository.findBooksByBookIds(bookIdList);

        for (Book book : books) {
            Optional<Checkout> checkout = checkoutList.stream()
                    .filter(x -> x.getBookId().equals(book.getId())).findFirst();

            if (checkout.isPresent()) {
                LocalDate firstDate = LocalDate.parse(checkout.get().getReturnDate());
                LocalDate secondDate = LocalDate.now();

                long differenceInTime = ChronoUnit.DAYS.between(secondDate, firstDate);

                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book.mapToDTO(), (int) differenceInTime));
            }
        }
        return shelfCurrentLoansResponses;
    }

    @Override
    @Transactional
    public void returnBook(String userEmail, Long bookId) throws Exception {
        Optional<Book> book = this.bookRepository.findById(bookId);

        Checkout validateCheckout = this.checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (book.isEmpty() || validateCheckout == null) {
            throw new ResourceNotFoundException("Book does not exist or not checked by user");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        this.bookRepository.save(book.get());

        LocalDate date1 = LocalDate.parse(validateCheckout.getReturnDate());
        LocalDate date2 = LocalDate.now();

        long differenceInTime = ChronoUnit.DAYS.between(date2, date1);

        if (differenceInTime < 0) {
            Optional<Payment> payment = this.paymentRepository.findByUserEmail(userEmail);
            if (payment.isEmpty()) {
                throw new ResourceNotFoundException("Internal server error. Something really bad happened.");
            }
            payment.get().setAmount(
                    payment.get().getAmount().add(
                            BigDecimal.valueOf(differenceInTime * -200, 2)
                    )
            );

            this.paymentRepository.save(payment.get());
        }

        this.checkoutRepository.deleteById(validateCheckout.getId());

        History history = new History(userEmail, validateCheckout.getCheckoutDate(), LocalDate.now().toString(),
                                      book.get().getTitle(), book.get().getAuthor(), book.get().getDescription(),
                                      book.get().getImg());

        historyRepository.save(history);
    }

    @Override
    @Transactional
    public void renewLoan(String userEmail, Long bookId) throws Exception {
        Checkout validateCheckout = this.checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (validateCheckout == null) {
            throw new ResourceNotFoundException("Book does not exist or not checked out by a user.");
        }

        LocalDate returnDate = LocalDate.parse(validateCheckout.getReturnDate());
        LocalDate todayDate = LocalDate.now();

        if (todayDate.isBefore(returnDate) || todayDate.isEqual(returnDate)) {
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }
}
