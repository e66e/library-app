package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dao.BookRepository;
import com.ebbe3000.spring_boot_library.dao.ReviewRepository;
import com.ebbe3000.spring_boot_library.dto.ReviewDTO;
import com.ebbe3000.spring_boot_library.entity.Review;
import com.ebbe3000.spring_boot_library.exception.ResourceNotFoundException;
import com.ebbe3000.spring_boot_library.requestmodel.ReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @Override
    public Page<ReviewDTO> findAllReviewsByBookId(Long id, int page, int pageSize) {

        this.bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id=" + id + " not found."));

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Review> reviews = this.reviewRepository.findByBookId(id, pageable);

        return new PageImpl<>(reviews.stream().map(Review::mapToDTO).toList(),
                                                    reviews.getPageable(),
                                                    reviews.getTotalElements());
    }

    @Override
    @Transactional
    public void postReview(String userEmail, ReviewRequest reviewRequest) throws Exception {
        Review validateReview = this.reviewRepository.findByUserEmailAndBookId(userEmail, reviewRequest.bookId());
        if (validateReview != null) {
            throw new Exception("Review already created.");
        }

        Review review = new Review();
        review.setBookId(reviewRequest.bookId());
        review.setUserEmail(userEmail);
        review.setRating(reviewRequest.rating());
        if (reviewRequest.reviewDescription().isPresent()) {
            review.setReviewDescription(reviewRequest.reviewDescription().map(
                    Object::toString
            ).orElse(null));
        }
        reviewRepository.save(review);
    }

    @Override
    public boolean userReviewListed(String userEmail, Long bookId) {
        Review validateReview = this.reviewRepository.findByUserEmailAndBookId(userEmail, bookId);
        return validateReview != null;
    }


}
