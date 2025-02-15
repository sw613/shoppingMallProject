package com.project.elice2.comment.domain;

import com.project.elice2.global.BaseEntity;
import com.project.elice2.product.domain.Product;
import com.project.elice2.recomment.domain.Recomment;
import com.project.elice2.recomment.domain.RecommentResponseDto;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private boolean isSecret;

    private LocalDateTime atDelete;

    @OneToOne(mappedBy = "comment", fetch = FetchType.LAZY)
    private Recomment recomment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public void updateComment(CommentRequestDto commentRequestDto) {
        this.detail = commentRequestDto.getDetail();
        this.isSecret = commentRequestDto.getIsSecret();
    }

    public void setAtDelete() {
        // comment soft delete
        this.atDelete = LocalDateTime.now();

        // recomment soft delete
        if (this.recomment != null) {
            this.recomment.setAtDelete();
        }
    }

    public CommentResponseDto commentToCommentResponseDto() {

        Long recommentId = null;
        RecommentResponseDto recommentResponseDto = null;

        if (this.recomment != null) {
            recommentId = this.recomment.getId();
            recommentResponseDto = recomment.recommentToRecommentResponseDto();
        }

        return new CommentResponseDto(this.id, this.detail, this.isSecret, recommentId, this.users.getId(), this.product.getId(), this.users.getUsername(), recommentResponseDto, this.getAtCreate(), this.getAtUpdate());
    }
}