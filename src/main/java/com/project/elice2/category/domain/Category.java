package com.project.elice2.category.domain;

import java.time.LocalDateTime;

import com.project.elice2.category.dto.ResponseDto;
import com.project.elice2.global.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetType petType;
    
    private LocalDateTime atDelete;
    
	
	public ResponseDto toResponseDto() {
		return ResponseDto.builder()
				.id(id)
				.name(name)
				.petType(petType)
				.build();
	}
}
