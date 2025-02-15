package com.project.elice2.category.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.elice2.category.domain.PetType;
import com.project.elice2.category.dto.RequestDto;
import com.project.elice2.category.service.CategoryService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("/admin/cate")
@Controller
public class CategoryController {
	
	private final CategoryService categoryService;
	
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	// 임시 관리자 페이지
	@GetMapping("/home")
	public String testPage(Model model) {		
		model.addAttribute("dogCategories", categoryService.findCategoryByType(PetType.DOG));
		model.addAttribute("catCategories", categoryService.findCategoryByType(PetType.CAT));
		
		return "category/cate-home";
	}
	
	// 카테고리 추가 페이지
	// 관리자 페이지에서 사용
	@GetMapping("/create")
	public String createCategoryPage(Model model) {	
		model.addAttribute("requestDto", new RequestDto()); // 빈 껍데기 객체
		
		return "category/cate-create";
	}
	
	// 카테고리 추가
	// 관리자 페이지에서 사용
	@PostMapping("/create")
	public String createCategory(@Valid @ModelAttribute RequestDto requestDto, BindingResult result) {	
		if(result.hasErrors()) {
			result.getAllErrors().forEach(error -> log.debug("Creating Category : Validation Error: {}", error.toString()));
			
			return "category/cate-create";
		}
		
		categoryService.addCategory(requestDto);

		return "redirect:/admin/cate/home"; // 관리자 페이지로 이동
	}
	
	// 카테고리 수정 페이지
	// 관리자 페이지에서 사용
	@GetMapping("/update/{id}")
	public String updateCategoryPage(@PathVariable Long id, Model model) {
		model.addAttribute("requestDto", new RequestDto()); // 빈 껍데기 객체
		model.addAttribute("category", categoryService.findCategory(id));
		
		return "category/cate-update";
	}
	
	// 카테고리 수정
	// 관리자 페이지에서 사용
	@PostMapping("/update")
	public String updateCategory(@Valid @ModelAttribute RequestDto requestDto, 
								 BindingResult result, 
								 Long id,
								 Model model) {
		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(error -> log.debug("Updating Category id {} : Validation Error: {}", id, error.toString()));
			
			model.addAttribute("requestDto", requestDto);
			model.addAttribute("category", categoryService.findCategory(id));
			
			return "category/cate-update";
		}
		
		categoryService.updateCategory(id, requestDto);
		
		return "redirect:/admin/cate/home"; // 관리자 페이지로 이동
	}
	
	// 카테고리 삭제
	// 관리자 페이지에서 사용
	@PostMapping("/delete/{id}")
	public String deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		
		return "redirect:/admin/cate/home"; // 관리자 페이지로 이동
	}
}
