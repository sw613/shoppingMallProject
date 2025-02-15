package com.project.elice2.category.dto;

import com.project.elice2.category.domain.Category;
import com.project.elice2.category.domain.PetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
	@NotBlank(message = "카테고리명은 비어있으면 안됩니다.")
	@Size(min = 1, max =10, message = "10글자 이하로 입력해 주세요.")
	private String name;
	
	@NotNull
	private PetType petType;
	
	
	public Category toCategory() {
		return Category.builder()
				.name(name)
				.petType(petType)
				.build();
	}
	
	public RequestDto(String name) {
		this.name = name;
	}
}
