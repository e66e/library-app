package com.ebbe3000.spring_boot_library.controller;


import com.ebbe3000.spring_boot_library.dto.ReviewDTO;
import com.ebbe3000.spring_boot_library.requestmodel.ReviewRequest;
import com.ebbe3000.spring_boot_library.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping(value = "/search/findBookById")
    public PagedModel<ReviewDTO> getReviewsByBookId(
            @RequestParam(name = "bookId") Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int pageSize
    ) {
        Page<ReviewDTO> allReviewsByBookId = this.reviewService.findAllReviewsByBookId(id, page, pageSize);

        return new PagedModel<>(allReviewsByBookId);
    }

    @PostMapping("/secure")
    public void postReview(JwtAuthenticationToken jwtAuthenticationToken,
                           @RequestBody ReviewRequest reviewRequest) throws Exception {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new Exception("User email is missing.");
        }
        reviewService.postReview(userEmail, reviewRequest);
    }

    @GetMapping("/secure/user/book")
    public Boolean reviewBookByUser(JwtAuthenticationToken jwtAuthenticationToken,
                                    @RequestParam Long bookId) throws Exception {
        String userEmail = jwtAuthenticationToken.getToken().getClaimAsString("email");
        if (userEmail == null) {
            throw new Exception("User email is missing.");
        }
        return this.reviewService.userReviewListed(userEmail, bookId);
    }
}
