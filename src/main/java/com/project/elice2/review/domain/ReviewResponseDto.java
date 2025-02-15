package com.project.elice2.review.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {


    private Long id;
    private Float score;
    private String detail;
    private LocalDateTime atCreate;

    private Long usersId;
    private String username;

    private Long productId;
    private String productTitle;

}
