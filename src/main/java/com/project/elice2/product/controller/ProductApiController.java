package com.project.elice2.product.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
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

import com.project.elice2.product.dto.RequestProductDto;
import com.project.elice2.product.dto.ResponseProductDto;
import com.project.elice2.product.service.ProductService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;


@RequestMapping("/api/product")
@RestController
public class ProductApiController {
	private final ProductService productService;

	public ProductApiController(ProductService productService) {
		this.productService =productService;
	}
	
	
	// 카테고리 id별 상품 조회
	@GetMapping
	@Operation(summary = "상품 조회", description = "category별로 상품 조회")
	public ResponseEntity<Page<ResponseProductDto>> getProducts(@RequestParam Long categoryId, 
																@RequestParam(defaultValue = "0") int page,
																@RequestParam(defaultValue = "10") int size) {
		Page<ResponseProductDto> products = productService.findProductsByCategory(categoryId, page, size);

		return ResponseEntity.ok(products);
	}
	
	// 단일 상품 조회
	@GetMapping("/{id}")
	@Operation(summary = "상품 조회", description = "상품 id로 상품 단일 조회")
	public ResponseEntity<ResponseProductDto> getProduct(@PathVariable Long id) {
		ResponseProductDto product = productService.findProduct(id);

		return ResponseEntity.ok(product);
	}
	
	// 상품 등록
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "상품 추가", description = "새로운 상품 생성")
	public ResponseEntity<ResponseProductDto> createProduct(@ModelAttribute RequestProductDto requestDto, 
															@RequestParam Long categoryId,
															@CurrentUser Users users) throws IOException {		
		
		if(requestDto.getFile() != null && !requestDto.getFile().isEmpty()) {
			productService.uploadFile(requestDto, requestDto.getFile());
		}
		
		ResponseProductDto addProduct = productService.addProduct(requestDto, categoryId, users);

		
		return ResponseEntity.status(HttpStatus.CREATED).body(addProduct);
	}
	
	// 상품 수정
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "상품 수정", description = "상품 id로 해당 상품 수정")
	public ResponseEntity<ResponseProductDto> updateProduct(@PathVariable Long id, @ModelAttribute RequestProductDto requestDto) throws IOException {		
        
		if(requestDto.getFile() != null && !requestDto.getFile().isEmpty()) {
			productService.uploadFile(requestDto, requestDto.getFile());
		} 
		
		ResponseProductDto updateProduct = productService.updateProduct(id, requestDto);

		return ResponseEntity.ok(updateProduct);
	}
	
	// 상품 삭제(논리삭제)
	@PutMapping("/delete/{id}")
	@Operation(summary = "상품 삭제", description = "상품 id로 해당 상품 삭제(논리삭제)")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		
		return ResponseEntity.ok("Category deleted successfully");
	}	
}
