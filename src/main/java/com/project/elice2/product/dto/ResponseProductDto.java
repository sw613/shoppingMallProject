package com.project.elice2.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseProductDto {
    private Long id;
	private String title;
    private String detail;
    private String reqImage;
    private Long viewCount;
    private Long price;
    private Long count;
    private Long categoryId;
    private String categoryName;
    private String petType;
}
