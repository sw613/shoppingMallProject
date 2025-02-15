package com.project.elice2.review.service;

import com.project.elice2.category.domain.Category;
import com.project.elice2.category.domain.PetType;
import com.project.elice2.product.domain.Product;
import com.project.elice2.review.domain.Review;
import com.project.elice2.review.repository.ReviewRepository;
import com.project.elice2.users.domain.Authority;
import com.project.elice2.users.domain.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    void createReview() {
        // given
        Users users = new Users();
        users.setId(1L);
        users.setUsername("user");
        users.setEmail("user@com");
        users.setPassword("1234");
        users.setAuthority(Authority.ROLE_USER);
        users.setProvider(null);

        Category category = new Category();
        category.setId(1L);
        category.setName("CAT");
        category.setPetType(PetType.CAT);

        Product product = new Product();
        product.setId(1L);
        product.setTitle("product name");
        product.setDetail("product detail");
        product.setPrice(10000L);
        product.setCategory(category);
        product.setCount(100L);
        product.setViewCount(0L);
        product.setReqImage("imageURL");
        product.setUsers(users);

        Review review = Review.builder()
                .id(1L)
                .score(4.5F)
                .detail("good product!")
                .users(users)
                .product(product)
                .build();

//        when(reviewRepository.save(any(Review.class))).thenReturn(review);
//        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // when
//        reviewRepository.save(review);

        // then
//        Review findReview = reviewRepository.findById(review.getId()).orElseThrow(() -> new RuntimeException("Not Fount Review"));
//        assertThat(findReview).isEqualTo(review);
//        assertThat(findReview.getId()).isEqualTo(review.getId());

    }

    @Test
    void findByUserId() {
    }

    @Test
    void findByProductId() {
    }

    @Test
    void updateReview() {
    }

    @Test
    void deleteReview() {
    }
}