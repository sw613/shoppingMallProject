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
public class ResponseProductListDto {

	private Long optionId;
	private String title;
	private Long price;
	private Long count;
    private String reqImage;
	
}
