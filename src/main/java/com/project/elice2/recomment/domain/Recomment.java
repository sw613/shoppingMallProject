package com.project.elice2.recomment.domain;

import com.project.elice2.comment.domain.Comment;
import com.project.elice2.global.BaseEntity;
import com.project.elice2.product.domain.Product;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recomment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recomment_id")
    private Long id;

    @Column(nullable = false)
    private String detail;

    private LocalDateTime atDelete;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public void updateRecommnt(RecommentRequestDto recommentRequestDto) {
        this.detail = recommentRequestDto.getDetail();
    }

    public void setAtDelete() {
        this.atDelete = LocalDateTime.now();
    }

    public RecommentResponseDto recommentToRecommentResponseDto() {
        return new RecommentResponseDto(this.id, this.detail, this.comment.getId(), this.getAtCreate(), this.getAtUpdate());
    }
}