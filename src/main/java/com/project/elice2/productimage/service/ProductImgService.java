package com.project.elice2.productimage.service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.elice2.fileUpload.FileUploadUtil;
import com.project.elice2.product.domain.Product;
import com.project.elice2.product.repository.ProductRepository;
import com.project.elice2.productimage.domain.ProductImage;
import com.project.elice2.productimage.dto.RequestProductImgDto;
import com.project.elice2.productimage.dto.ResponseProductImgDto;
import com.project.elice2.productimage.repository.ProductImgRepository;


@Service
public class ProductImgService {
	
	private final ProductImgRepository productImgRepository;
	private final ProductRepository productRepository;
	private final FileUploadUtil fileUploadUtil;
	
	public ProductImgService(ProductImgRepository productImgRepository, ProductRepository productRepository, FileUploadUtil fileUploadUtil) {
		this.productImgRepository = productImgRepository;
		this.productRepository = productRepository;
		this.fileUploadUtil = fileUploadUtil;
	}
	
	// product별 이미지 조회(브라우저에서 조회 시 사용)
	@Transactional(readOnly = true)
	public List<ResponseProductImgDto> findProductImages(Long productId) {
		Product product = productRepository.findByIdAndAtDeleteIsNull(productId).orElseThrow(() -> new NoSuchElementException("Not Found Product id : " + productId));
		
		List<ProductImage> productImages = productImgRepository.findByProduct(product);

	    return productImages.stream()
	            .map(productImg -> ResponseProductImgDto.builder()
	                    .id(productImg.getId())
	                    .url(fileUploadUtil.getS3(productImg.getUrl()))
	                    .productId(productId)
	                    .build())
	            .collect(Collectors.toList());
	}
	
	
	// product별 이미지 조회(db에 저장된 url 반환, 수정 시 사용)
	@Transactional(readOnly = true)
	public List<ResponseProductImgDto> getProductImages(Long productId) {
		Product product = productRepository.findByIdAndAtDeleteIsNull(productId).orElseThrow(() -> new NoSuchElementException("Not Found Product id : " + productId));
		
		List<ProductImage> productImages = productImgRepository.findByProduct(product);
		
		return productImages.stream()
				.map(ProductImage::toProductImgDto)
				.collect(Collectors.toList());
	}
	
		
	// 이미지 단일 조회
	@Transactional(readOnly = true)
	public ResponseProductImgDto findProductIamge(Long id) {
		ProductImage productImage = productImgRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found ProductImg id : " + id));
		
    	return ResponseProductImgDto.builder()
    			.id(id)
    			.url(fileUploadUtil.getS3(productImage.getUrl()))
    			.productId(productImage.getProduct().getId())
    			.build();
	}
	
	// 이미지 업로드
	public void uploadFile(RequestProductImgDto requestDto, MultipartFile file) throws IOException {
    	fileUploadUtil.upload(requestDto, file); 
	}
	
	// 이미지 추가
	@Transactional
	public ResponseProductImgDto addProductImg(RequestProductImgDto requestDto, Long productId) {
		ProductImage productImage = requestDto.toProductImage();
		productImage.setProduct(productRepository.findByIdAndAtDeleteIsNull(productId).orElseThrow(() -> new NoSuchElementException("Not Found Product id : " + productId)));
		
		ProductImage saveProductImage = productImgRepository.save(productImage);
		
		return saveProductImage.toProductImgDto();
	}
	
	// 이미지 수정
	@Transactional
	public ResponseProductImgDto updateProductImage(RequestProductImgDto requestDto, Long id) {
		ProductImage productImage = productImgRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found ProductImg id : " + id));
		
		Optional.ofNullable(requestDto.getUrl()).ifPresent(productImage::setUrl);
		ProductImage updateProductImage = productImgRepository.save(productImage);
		
		return updateProductImage.toProductImgDto();
	}
	

	// 이미지 파일 삭제
	@Transactional
	public void deleteImageFile(String url) throws IOException {	
		// db에서 삭제
		ProductImage productImage = productImgRepository.findByUrl(url).orElseThrow(() -> new IllegalArgumentException("The image URL is invalid"));
		productImgRepository.delete(productImage);
		
		// 실제 파일 삭제
		fileUploadUtil.deleteS3(url);
	}
	
	// 이미지 삭제
	@Transactional
	public void deleteProductImg(Long id) {
		ProductImage productImage = productImgRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Not Found ProductImg id : " + id));
		
		productImgRepository.delete(productImage);
	}
}
