package com.project.elice2.product.dto;

import java.util.List;

import com.project.elice2.productimage.dto.RequestProductImgDto;
import com.project.elice2.puductoption.dto.RequestOptionDto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter
@Setter
public class RequestProductFullDto {
    @Valid
    private RequestProductDto product;

    @Valid
    private RequestProductImgDto productImages;

    @Valid
    private List<@Valid RequestOptionDto> options;
}
