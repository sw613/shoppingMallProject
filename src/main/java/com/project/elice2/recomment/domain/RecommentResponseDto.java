package com.project.elice2.recomment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecommentResponseDto {

    private Long id;
    private String detail;
    private Long commentId;

    private LocalDateTime atCreate;
    private LocalDateTime atUpdate;
}