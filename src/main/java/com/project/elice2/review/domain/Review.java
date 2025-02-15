package com.project.elice2.review.domain;

import com.project.elice2.global.BaseEntity;
import com.project.elice2.product.domain.Product;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false)
    private Float score;

    @Column(nullable = false)
    private String detail;

    private LocalDateTime atDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public Review() {
    }

    public void updateReview(Float score, String detail) {
        this.score = score;
        this.detail = detail;
    }

    public void setAtDelete() {
        this.atDelete = LocalDateTime.now();
    }


    public ReviewResponseDto reviewToReviewResponseDto() {
        return new ReviewResponseDto(id, score, detail, this.getAtCreate(), users.getId(), users.getUsername(), product.getId(), product.getTitle());
    }
}
