package com.project.elice2.productimage.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.elice2.productimage.dto.RequestProductImgDto;
import com.project.elice2.productimage.service.ProductImgService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("/admin/productImg")
@Controller
public class ProductImgController {

	private final ProductImgService productImgService;
	
	public ProductImgController(ProductImgService productImgService) {
		this.productImgService = productImgService;
	}
	
	
	// 상품별 상품 이미지 리스트 조회
	@GetMapping("/list")
	public String getProductImages(Model model, Long productId) {
		model.addAttribute("productImages", productImgService.findProductImages(productId));
		
		return "";   
	}
	
	// 상품 이미지 추가 페이지
	@GetMapping("/create")
	public String createProductImgPage(Model model) {	
		
		return "";
	}
	
	
	// 상품 이미지 추가
	@PostMapping("/create")
	public String createProductImg(@Valid @ModelAttribute RequestProductImgDto requestDto, 
									BindingResult result,
									Long productId) throws IOException {
		if(result.hasErrors()) {
			return "";
		}
		
		if(requestDto.getDetailFile() != null && !requestDto.getDetailFile().isEmpty()) {
			for(MultipartFile mf : requestDto.getDetailFile()) {
				productImgService.uploadFile(requestDto, mf);
			}
		}
		
		
		productImgService.addProductImg(requestDto, productId);
		
		return "redirect:/";
	}
	
	// 상품 이미지 수정 페이지
	@GetMapping("/update/{productId}")
	public String updateProductImgPage(@PathVariable Long productId, Model model) {		
		model.addAttribute("images", productImgService.getProductImages(productId));
		model.addAttribute("productId", productId);
		model.addAttribute("requestDto", new RequestProductImgDto()); // 임시객체

		return "product/product-img-update";
	}
	
	// 상품 이미지 수정
	@PostMapping("/update")
	public String updateProductImg(@Valid @ModelAttribute RequestProductImgDto requestDto, 
									BindingResult result,
									@RequestParam(required = false) List<String> deletedImages, // 기존 업로드된 사진 중 삭제된 이미지
									Long productId,
									Model model,
									HttpServletResponse response) throws IOException {		
		if(result.hasErrors()) {
			result.getAllErrors().forEach(error -> log.debug("Updating Product Detial Images : Product id : {},Validation Error: {}", productId, error.toString()));
			
			model.addAttribute("images", productImgService.findProductImages(productId));
			model.addAttribute("productId", productId);
			model.addAttribute("requestDto", requestDto);
			
	        // HTTP 상태 코드 400으로 응답
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			return "product/product-img-update";
		}
		
	    // 기존 이미지 삭제		
		if(deletedImages != null && !deletedImages.isEmpty()) {
			for(String deletedImg : deletedImages) {
				productImgService.deleteImageFile(deletedImg);
			}
		}
		
		// 새로 추가된 이미지 저장
		if(requestDto.getDetailFile() != null && !requestDto.getDetailFile().isEmpty()) {
			for(MultipartFile file : requestDto.getDetailFile()) {
				productImgService.uploadFile(requestDto, file);
	
				productImgService.addProductImg(requestDto, productId);
			}			
		}

		return "redirect:/admin/product";
	}
	
	// 상품 이미지 삭제
	@PostMapping("/delete/{id}")
	public String deleteProductImg(@PathVariable Long id) {
		productImgService.deleteProductImg(id);
		
		return "redirect:/";
	}
}