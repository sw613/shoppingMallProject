package com.project.elice2.puductoption.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.elice2.puductoption.dto.RequestOptionDto;
import com.project.elice2.puductoption.dto.RequestOptionListDto;
import com.project.elice2.puductoption.service.ProductOptionService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("/admin/option")
@Controller
public class ProductOptionController {
	private final ProductOptionService optionService;
	
	public ProductOptionController(ProductOptionService optionService) {
		this.optionService = optionService;
	}
	
	
	// product별 옵션 조회
	@GetMapping("/list")
	public String getOptions(@RequestParam Long productId, Model model) {
		model.addAttribute("options", optionService.findOptions(productId));

		return "";
	}
	
	// 단일 옵션 조회
	@GetMapping("/{id}")
	public String getOption(@PathVariable Long id , Model model) {
		model.addAttribute("option", optionService.findOption(id));

		return "";
	}
	
	// 옵션 등록 페이지
	@GetMapping("/create")
	public String createOptionPage() {		
		
		return "";
	}
	
	// 옵션 등록
	@PostMapping("/create")
	public String createOption(@Valid RequestOptionDto requestDto, Long productId, BindingResult result) {
		if(result.hasErrors()) {
			return "";
		}
		
		optionService.addOption(requestDto, productId);
		
		return "redirect:/";
	}
	
	// 옵션 수정 페이지
	@GetMapping("/update/{productId}")
	public String updateOptionPage(@PathVariable Long productId, Model model) {			
		model.addAttribute("options", optionService.getOptions(productId));
		model.addAttribute("productId", productId);
	    model.addAttribute("requestOptionListDto", new RequestOptionListDto()); // 임시객체

		return "product/product-option-update";
	}
	
	// 옵션 수정
	@PostMapping("/update")
	public String updateOption(@Valid @ModelAttribute RequestOptionListDto requestOptionListDto,
							   BindingResult result,
							   Long productId,
							   Model model,
							   HttpServletResponse response) throws IOException {		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(error -> log.debug("Updating Option : Product id : {}, Validation Error: {}", productId, error.toString()));
			
		    Map<String, String> fieldErrors = result.getFieldErrors().stream()
	                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));  // 에러 map형태로 반환
			
			model.addAttribute("options", optionService.getOptions(productId));
			model.addAttribute("requestOptionListDto", requestOptionListDto);
			model.addAttribute("productId", productId);
			
			model.addAttribute("fieldErrors", fieldErrors);
			
	        // HTTP 상태 코드 400으로 응답
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			return "product/product-option-update";
		}
		
		List<RequestOptionDto> requestOptionDtos = requestOptionListDto.getRequestOptionDtos();
		
		if(requestOptionDtos != null && !requestOptionDtos.isEmpty()) {
			optionService.updateOptions(productId, requestOptionDtos);		
			 
		}

		return "redirect:/admin/product";
	}
	
	
	// 옵션 삭제
	@PostMapping("/delete/{id}")
	public String deleteOption(@PathVariable Long id) {
		optionService.deleteOption(id);
		
		return "redirect:/";
	}	
}