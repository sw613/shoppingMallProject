package com.project.elice2.puductoption.dto;

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
public class ResponseOptionDto {
    private Long id;
    private String optionName;
    private Long optionPrice;
    private Long optionCount;
    private Long productId;
}
