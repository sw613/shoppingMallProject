package com.project.elice2.category.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.elice2.category.domain.Category;
import com.project.elice2.category.domain.PetType;
import com.project.elice2.category.dto.RequestDto;
import com.project.elice2.category.dto.ResponseDto;
import com.project.elice2.category.repository.CategoryRepository;

import jakarta.transaction.Transactional;


@Service
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	// 동물 타입별 모든 카테고리 조회
	public List<ResponseDto> findCategoryByType(PetType petType) {
		List<Category> categories = categoryRepository.findByPetTypeAndAtDeleteIsNull(petType);
		
		return categories.stream()
				.map(Category::toResponseDto)
				.collect(Collectors.toList());
	}
	
	// 특정 카테고리 조회
	public ResponseDto findCategory(Long id) {
		Category category = categoryRepository.findByIdAndAtDeleteIsNull(id).orElseThrow(() -> new NoSuchElementException("Not Found category id: " + id));
		
		return category.toResponseDto();
	}
	
	// 카테고리 추가
	@Transactional
	public ResponseDto addCategory(RequestDto requestDto) {
		Category category = requestDto.toCategory();
		Category addCategory = categoryRepository.save(category);
		
		return addCategory.toResponseDto();
	}
	
	// 카테고리 수정
	@Transactional
	public ResponseDto updateCategory(Long id, RequestDto requestDto) {
		Category category = categoryRepository.findByIdAndAtDeleteIsNull(id).orElseThrow(() -> new NoSuchElementException("Not Found category id: " + id));
		
		Optional.ofNullable(requestDto.getPetType()).ifPresent(category::setPetType);
		Optional.ofNullable(requestDto.getName()).ifPresent(category::setName);
		
		Category updateCategory = categoryRepository.save(category);
		
		return updateCategory.toResponseDto();
	}
	
	// 카테고리 삭제(논리 삭제)
	@Transactional
	public void deleteCategory(Long id) {
		LocalDateTime currentTime = LocalDateTime.now();
		
		Category category = categoryRepository.findByIdAndAtDeleteIsNull(id).orElseThrow(() -> new NoSuchElementException("Not Found category id: " + id));
		
		category.setAtDelete(currentTime);		
		categoryRepository.save(category);
		
	}
}
