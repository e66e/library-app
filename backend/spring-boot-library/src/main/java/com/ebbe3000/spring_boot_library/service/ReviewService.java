package com.ebbe3000.spring_boot_library.service;

import com.ebbe3000.spring_boot_library.dto.ReviewDTO;
import com.ebbe3000.spring_boot_library.requestmodel.ReviewRequest;
import org.springframework.data.domain.Page;

public interface ReviewService {

    Page<ReviewDTO> findAllReviewsByBookId(Long id, int page, int pageSize);
    void postReview(String userEmail, ReviewRequest reviewRequest) throws Exception;
    boolean userReviewListed(String userEmail, Long bookId);
}
