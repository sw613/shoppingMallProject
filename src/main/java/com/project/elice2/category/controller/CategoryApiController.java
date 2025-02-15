package com.project.elice2.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.elice2.category.domain.PetType;
import com.project.elice2.category.dto.RequestDto;
import com.project.elice2.category.dto.ResponseDto;
import com.project.elice2.category.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;


@RequestMapping("/api/cate")
@RestController
public class CategoryApiController {
	
	private final CategoryService categoryService;
	
	public CategoryApiController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GetMapping
	@Operation(summary = "카테고리 조회", description = "petType별로 카테고리 조회")
	public ResponseEntity<List<ResponseDto>> getCategories(@RequestParam PetType petType) {
		List<ResponseDto> categories= categoryService.findCategoryByType(petType);
		
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/{id}")
	@Operation(summary = "카테고리 조회", description = "카테고리 id로 카테고리 단일 조회")
	public ResponseEntity<ResponseDto> getCategory(@PathVariable Long id) {
		ResponseDto categoryDto = categoryService.findCategory(id);
		
		return ResponseEntity.ok(categoryDto);
	}
	
	@PostMapping
	@Operation(summary = "카테고리 추가", description = "새로운 카테고리 생성")
	public ResponseEntity<ResponseDto> createCategory(@RequestBody RequestDto requestDto) {
		ResponseDto addcategory = categoryService.addCategory(requestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(addcategory);
	}
	
	@PutMapping("/{id}")
	@Operation(summary = "카테고리 수정", description = "카테고리 id로 해당 카테고리 수정")
	public ResponseEntity<ResponseDto> updateCategory(@PathVariable Long id, @RequestBody RequestDto requestDto) {		
		ResponseDto updatedCategory = categoryService.updateCategory(id, requestDto);

		return  ResponseEntity.ok(updatedCategory);
	}
	
	@PutMapping("/delete/{id}")
	@Operation(summary = "카테고리 삭제", description = "카테고리 id로 해당 카테고리 삭제(논리삭제)")
	public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		
		return ResponseEntity.ok("Category deleted successfully");
	}
}
