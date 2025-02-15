package com.project.elice2.review.service;

import com.project.elice2.product.domain.Product;
import com.project.elice2.product.repository.ProductRepository;
import com.project.elice2.review.domain.Review;
import com.project.elice2.review.domain.ReviewRequestDto;
import com.project.elice2.review.domain.ReviewResponseDto;
import com.project.elice2.review.repository.ReviewRepository;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponseDto createReview(Users users, ReviewRequestDto reviewRequestDto) {

        // FIXME 임시 코드 users가 null 일때
        if (users == null) {
            users = userRepository.findById(1L).orElseThrow();
        }

        Product product = productRepository.findById(reviewRequestDto.getProductId()).orElseThrow();

        // 리뷰 생성
        Review review = Review.builder()
                .score(reviewRequestDto.getScore())
                .detail(reviewRequestDto.getDetail())
                .users(users)
                .product(product)
                .build();

        // 리뷰 저장
        reviewRepository.save(review);

        return review.reviewToReviewResponseDto();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> findByUserId(Long userId) {
        // userId가 같고 atDelte 필드가 null인 review 구하기
        List<Review> reviews = reviewRepository.findByUsers_IdAndAtDeleteIsNull(userId);

        return reviews.stream()
                .map(review -> review.reviewToReviewResponseDto())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> findByProductId(Long productId) {
        // productId가 같고 atDelte 필드가 null인 review 구하기
        List<Review> reviews = reviewRepository.findByProduct_IdAndAtDeleteIsNull(productId);

        return reviews.stream()
                .map(review -> review.reviewToReviewResponseDto())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> findByProductId(Long productId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByProduct_IdAndAtDeleteIsNull(productId, pageable);

        return reviewPage.map(review -> review.reviewToReviewResponseDto());
    }

    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto reviewRequestDto) {

        // 리뷰 reviewId 로 가져오기
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(() -> new NoSuchElementException("Not Found review id: " + reviewId));

        // 리뷰 업데이트
        findReview.updateReview(reviewRequestDto.getScore(), reviewRequestDto.getDetail());

        // 리뷰 저장
        reviewRepository.save(findReview);

        return findReview.reviewToReviewResponseDto();
    }

    @Transactional
    public Long deleteReview(Long reviewId) {
        // 삭제할 Review 찾기
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(() -> new NoSuchElementException("Not Found review id: " + reviewId));

        // soft delete
        findReview.setAtDelete();

        reviewRepository.save(findReview);

        return findReview.getProduct().getId();
    }

    // 리뷰를 작성했엇는지 체크
    @Transactional(readOnly = true)
    public Boolean checkReviewWriter(Users users, Long productId) {
        if (users == null) {
            return false;
        }

        List<Review> findReviews = reviewRepository.findByProduct_IdAndAtDeleteIsNull(productId);

        return findReviews.stream().map(review -> review.getUsers()).anyMatch(users1 -> users1.getId().equals(users.getId()));
    }
}
