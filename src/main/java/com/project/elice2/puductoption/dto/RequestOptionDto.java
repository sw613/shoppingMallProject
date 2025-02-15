package com.project.elice2.puductoption.dto;

import com.project.elice2.puductoption.domain.ProductOption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestOptionDto {
	
	@NotBlank(message = "값을 입력해 주세요.")
	private String optionName;
    
    @NotNull(message = "상품 옵션 가격은 비어있으면 안됩니다.")
    @PositiveOrZero(message = "양수로 입력해 주세요.")
    private Long optionPrice;

    @NotNull(message = "상품 옵션 수량은 비어있으면 안됩니다.")
    @PositiveOrZero(message = "양수로 입력해 주세요.")
    private Long optionCount;
    
    public ProductOption toProductOption() {
    	return ProductOption.builder()
    			.name(optionName)
    			.price(optionPrice)
    			.count(optionCount)
    			.build();
    }
    
}
