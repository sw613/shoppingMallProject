package com.project.elice2.review.controller;

import com.project.elice2.review.domain.ReviewRequestDto;
import com.project.elice2.review.domain.ReviewResponseDto;
import com.project.elice2.review.service.ReviewService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto reviewRequestDto, @CurrentUser Users users) {

        ReviewResponseDto reviewResponseDto = reviewService.createReview(users,reviewRequestDto);

        return ResponseEntity.ok(reviewResponseDto);
    }

    @GetMapping("/findproduct/{productId}")
    public ResponseEntity<Page<ReviewResponseDto>> findByProductId(@PathVariable Long productId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
//        List<ReviewResponseDto> reviewResponseDtos = reviewService.findByProductId(productId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("atCreate").descending());

        Page<ReviewResponseDto> reviewResponseDtos = reviewService.findByProductId(productId, pageable);

        return ResponseEntity.ok(reviewResponseDtos);
    }

    @GetMapping("/finduser/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> findByUserId(@PathVariable Long userId) {
        List<ReviewResponseDto> reviewResponseDtos = reviewService.findByUserId(userId);

        return ResponseEntity.ok(reviewResponseDtos);
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<Void> updateReview(@PathVariable Long reviewId,@RequestBody ReviewRequestDto reviewRequestDto) {

        ReviewResponseDto reviewResponseDto = reviewService.updateReview(reviewId, reviewRequestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/product/" + reviewRequestDto.getProductId());  // 리디렉션할 URL

        return ResponseEntity.status(HttpStatus.FOUND)
                .headers(headers)
                .build();
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok("delete success");
    }
}
