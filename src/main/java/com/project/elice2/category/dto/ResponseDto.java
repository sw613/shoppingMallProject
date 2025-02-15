package com.project.elice2.category.dto;

import com.project.elice2.category.domain.PetType;

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
public class ResponseDto {
	private Long id;
	private String name;
	private PetType petType;
}
