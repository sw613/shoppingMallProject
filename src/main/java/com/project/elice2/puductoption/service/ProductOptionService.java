package com.project.elice2.puductoption.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.elice2.fileUpload.FileUploadUtil;
import com.project.elice2.product.domain.Product;
import com.project.elice2.product.dto.ResponseProductListDto;
import com.project.elice2.product.repository.ProductRepository;
import com.project.elice2.puductoption.domain.ProductOption;
import com.project.elice2.puductoption.dto.RequestOptionDto;
import com.project.elice2.puductoption.dto.ResponseOptionDto;
import com.project.elice2.puductoption.repository.ProductOptionRepository;


@Service
public class ProductOptionService {
	
	private final ProductOptionRepository optionRepository;
	private final ProductRepository productRepository;
	private final FileUploadUtil fileUploadUtil;
	
	public ProductOptionService(ProductOptionRepository optionRepository, ProductRepository productRepository, FileUploadUtil fileUploadUtil) {
		this.optionRepository = optionRepository;
		this.productRepository = productRepository;
		this.fileUploadUtil = fileUploadUtil;
	}
	
	// product 별 옵션 조회
	@Transactional(readOnly = true)
	public List<ResponseOptionDto> findOptions(Long productId) {
		Product product = productRepository.findByIdAndAtDeleteIsNull(productId).get();
		List<ProductOption> options = optionRepository.findByProduct(product);
		
		return options.stream()
				.map(ProductOption::toResponseDto)
				.collect(Collectors.toList());
	}
	
	// product 별 옵션 조회 (db옵션 가격 조회, 수정 시 사용)
	@Transactional(readOnly = true)
	public List<ResponseOptionDto> getOptions(Long productId) {
		Product product = productRepository.findByIdAndAtDeleteIsNull(productId).get();
		List<ProductOption> options = optionRepository.findByProduct(product);

		return options.stream().map(option -> ResponseOptionDto.builder()
						.id(option.getId())
						.optionName(option.getName())
						.optionPrice(option.getPrice())
						.optionCount(option.getCount())
						.productId(productId)
						.build()).collect(Collectors.toList());
	}
	
	
	// 옵션 단일 조회
	@Transactional(readOnly=true)
	public ResponseOptionDto findOption(Long id) {
		ProductOption option = optionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found option id: " + id));	
		
		return option.toResponseDto();
	}
	
	
	// 옵션 단일 조회(production_option 객체반환)
	@Transactional
	public ProductOption findProductOption(Long id) {
        return optionRepository.findWithLock(id);
	}
	
	
	// 옵션id List로 상품 정보 List 조회
    @Transactional
    public List<ResponseProductListDto> findProductList (List<Long> optionIdList) {
        List<ResponseProductListDto> responseDtos = new ArrayList<>(); 

        if(optionIdList != null && !optionIdList.isEmpty()) {
            for(Long optionId : optionIdList) {
                ProductOption option = optionRepository.findById(optionId).orElseThrow(() -> new NoSuchElementException("Not Found option id: " + optionId));

                Long productId = option.getProduct().getId();
                Product product = productRepository.findByIdAndAtDeleteIsNull(productId).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + productId));

                responseDtos.add(ResponseProductListDto.builder()
                        .optionId(optionId)
                        .title(product.getTitle() + " " + option.getName())
                        .price(option.getPrice() + product.getPrice())
                        .count(option.getCount())
                        .reqImage(fileUploadUtil.getS3(product.getReqImage()))
                        .build());
            }
        }

        return responseDtos;
    }
	
	
	// 옵션 등록
	@Transactional
	public ResponseOptionDto addOption(RequestOptionDto requestDto, Long productId) {
		ProductOption option = requestDto.toProductOption();
		option.setProduct(productRepository.findByIdAndAtDeleteIsNull(productId).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + productId)));	
		
		ProductOption addOption = optionRepository.save(option);
		
		return addOption.toResponseDto();
	}	
	
	// 옵션 단일 수정
	@Transactional
	public ResponseOptionDto updateOption(Long id, RequestOptionDto requestDto) {
		ProductOption option = optionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found option id: " + id));	
	
		Optional.ofNullable(requestDto.getOptionName()).ifPresent(option::setName);
		Optional.ofNullable(requestDto.getOptionPrice()).ifPresent(option::setPrice);
		Optional.ofNullable(requestDto.getOptionCount()).ifPresent(option::setCount);
		
		ProductOption updateOption = optionRepository.save(option);
		
		return updateOption.toResponseDto();
	}
	
	
	// 옵션 리스트 수정(기존 옵션 삭제 후 옵션 새로 저장)
	@Transactional
    public void updateOptions(Long productId, List<RequestOptionDto> requestOptionDtos) {

        Product product = productRepository.findByIdAndAtDeleteIsNull(productId).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + productId));

        List<ProductOption> options = optionRepository.findByProduct(product);
        
        // 기존 옵션 삭제
        optionRepository.deleteAll(options);
        
        // 새로운 옵션 저장
        for(RequestOptionDto optionDto : requestOptionDtos) {
        	ProductOption newOption = optionDto.toProductOption();
        	newOption.setProduct(product);
        	
        	optionRepository.save(newOption);
        }
    }
	
	
	// 상품 옵션 재고 관리(주문 완료 시 재고수량 수정)
	@Transactional
	public void decreaseOptionCount(Long id, int quantity) {
		ProductOption option = optionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found option id: " + id));
		
		if(quantity > option.getCount()) {
			throw new IllegalStateException("Stock is insufficient");
		}
		option.setCount(option.getCount() -quantity);
		optionRepository.save(option);
	}
	
	 // 옵션 삭제
	 @Transactional
	 public void deleteOption(Long id) {
		 ProductOption option = optionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found option id: " + id));
		 
		 optionRepository.delete(option);
	 }	 
}