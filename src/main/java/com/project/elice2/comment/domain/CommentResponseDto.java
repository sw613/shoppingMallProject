package com.project.elice2.comment.domain;

import com.project.elice2.recomment.domain.RecommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String detail;
    private boolean isSecret;
    private Long recommentId;
    private Long userId;
    private Long productId;
    private String username;
    private RecommentResponseDto recomment;
    private LocalDateTime atCreate;
    private LocalDateTime atUpdate;

}