package com.project.elice2.productimage.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.elice2.productimage.dto.RequestProductImgDto;
import com.project.elice2.productimage.dto.ResponseProductImgDto;
import com.project.elice2.productimage.service.ProductImgService;

import io.swagger.v3.oas.annotations.Operation;


@RequestMapping("/api/productImg")
@RestController()
public class ProductImgApiController {
	
	private final ProductImgService productImgService;
	
	public ProductImgApiController(ProductImgService productImgService) {
		this.productImgService = productImgService;
	}
	
	
	@GetMapping
	@Operation(summary = "상품 이미지 조회", description = "productId 별로 상품 이미지 조회")
	public ResponseEntity<List<ResponseProductImgDto>> getProductImages(@RequestParam Long productId) {
		List<ResponseProductImgDto> productImages = productImgService.findProductImages(productId);
		
		return ResponseEntity.ok(productImages);
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "상품 이미지 조회", description = "productImgId로 상품 이미지 개별 조회")
	public ResponseEntity<ResponseProductImgDto> getProductImage(@RequestParam Long id) {
		ResponseProductImgDto productImage = productImgService.findProductIamge(id);
		
		return ResponseEntity.ok(productImage);
	}
	
	// 상품 이미지 등록
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "상품 이미지 추가", description = "새로운 상품 이미지 생성")
	public ResponseEntity<ResponseProductImgDto> createProductImage(@ModelAttribute RequestProductImgDto requestDto, @RequestParam Long productId) throws IOException {		
		
		if(requestDto.getDetailFile() != null && !requestDto.getDetailFile().isEmpty()) {
			for(MultipartFile mf : requestDto.getDetailFile()) {
				productImgService.uploadFile(requestDto, mf);
			}
		}
		
		ResponseProductImgDto addProductImage = productImgService.addProductImg(requestDto, productId);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(addProductImage);
	}
	
	// 상품 이미지 수정
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "상품 이미지 수정", description = "상품 이미지 id로 해당 상품 이미지 수정")
	public ResponseEntity<ResponseProductImgDto> updateProductImage(@PathVariable Long id, @ModelAttribute RequestProductImgDto requestDto) throws IOException {		
		
		if(requestDto.getDetailFile() != null && !requestDto.getDetailFile().isEmpty()) {
			for(MultipartFile mf : requestDto.getDetailFile()) {
				productImgService.uploadFile(requestDto, mf);
			}
		}
		
		ResponseProductImgDto updateProductImages = productImgService.updateProductImage(requestDto, id);

		return ResponseEntity.ok(updateProductImages);
	}
	
	// 상품 이미지 삭제
	@PutMapping("/delete/{id}")
	@Operation(summary = "상품 이미지 삭제", description = "상품 이미지 id로 해당 상품 이미지 삭제")
	public ResponseEntity<String> deleteProductImage(@PathVariable Long id) {
		productImgService.deleteProductImg(id);
		
		return ResponseEntity.ok("Category deleted successfully");
	}	
	
}
