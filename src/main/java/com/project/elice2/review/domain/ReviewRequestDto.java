package com.project.elice2.review.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {

//    @NotBlank(message = "평점을 입력해주세요.")
    private Float score;

//    @NotBlank(message = "리뷰를 남겨주세요.")
//    @Size(min = 1, max = 500, message = "리뷰는 500자까지 남길 수 있습니다.")
    private String detail;

//    @NotNull
    private Long productId;


    public void setScore(Float score) {
        this.score = score / 2f;
    }
}
