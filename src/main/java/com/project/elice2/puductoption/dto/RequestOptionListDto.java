package com.project.elice2.puductoption.dto;

import java.util.List;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RequestOptionListDto {
	
	@Valid
	private List<RequestOptionDto> requestOptionDtos;
}
