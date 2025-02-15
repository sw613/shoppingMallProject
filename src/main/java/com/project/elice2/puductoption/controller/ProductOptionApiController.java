package com.project.elice2.puductoption.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.elice2.product.dto.ResponseProductListDto;
import com.project.elice2.puductoption.dto.RequestOptionDto;
import com.project.elice2.puductoption.dto.ResponseOptionDto;
import com.project.elice2.puductoption.service.ProductOptionService;

import io.swagger.v3.oas.annotations.Operation;


@RequestMapping("/api/option")
@RestController
public class ProductOptionApiController {
	private final ProductOptionService optionService;
	
	public ProductOptionApiController(ProductOptionService optionService) {
		this.optionService = optionService;
	}
	
	
	// product별 옵션 조회
	@GetMapping
	@Operation(summary = "옵션 조회", description = "product별로 옵션 조회")
	public ResponseEntity<List<ResponseOptionDto>> getOptions(@RequestParam Long productId) {
		List<ResponseOptionDto> options = optionService.findOptions(productId);

		return ResponseEntity.ok(options);
	}
	
	// 단일 옵션 조회
	@GetMapping("/{id}")
	@Operation(summary = "옵션 조회", description = "옵션 id로 옵션 단일 조회")
	public ResponseEntity<ResponseOptionDto> getOption(@PathVariable Long id) {
		ResponseOptionDto option = optionService.findOption(id);

		return ResponseEntity.ok(option);
	}
	
	
	// 옵션id List로 상품 정보 List 조회
	@GetMapping("/products")
	@Operation(summary = "product list 조회", description = "옵션 id list로 product list 조회")
	public ResponseEntity<List<ResponseProductListDto>> getProductList(@RequestParam List<Long> optionIdList) {
		List<ResponseProductListDto> productList = optionService.findProductList(optionIdList);

		return ResponseEntity.ok(productList);
	}
	
	
	// 옵션 등록
	@PostMapping()
	@Operation(summary = "옵션 추가", description = "새로운 옵션 생성")
	public ResponseEntity<ResponseOptionDto> createOption(@RequestBody RequestOptionDto requestDto, Long productId) {		
		ResponseOptionDto addOption = optionService.addOption(requestDto, productId);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(addOption);
	}
	
	// 옵션 수정
	@PutMapping("/{id}")
	@Operation(summary = "옵션 수정", description = "옵션 id로 해당 옵션 수정")
	public ResponseEntity<ResponseOptionDto> updateOption(@PathVariable Long id, @RequestBody RequestOptionDto requestDto) {		
		ResponseOptionDto updateOption = optionService.updateOption(id, requestDto);

		return ResponseEntity.ok(updateOption);
	}
	
	// 옵션 삭제
	@DeleteMapping("/{id}")
	@Operation(summary = "옵션 삭제", description = "옵션 id로 해당 옵션 삭제")
	public ResponseEntity<String> deleteOption(@PathVariable Long id) {
		optionService.deleteOption(id);
		
		return ResponseEntity.ok("Product Option deleted successfully");
	}	
}
