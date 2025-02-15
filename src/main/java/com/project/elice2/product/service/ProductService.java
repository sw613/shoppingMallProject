package com.project.elice2.product.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.elice2.category.domain.Category;
import com.project.elice2.category.domain.PetType;
import com.project.elice2.category.repository.CategoryRepository;
import com.project.elice2.fileUpload.FileUploadUtil;
import com.project.elice2.product.domain.Product;
import com.project.elice2.product.dto.RequestProductDto;
import com.project.elice2.product.dto.ResponseProductDto;
import com.project.elice2.product.repository.ProductRepository;
import com.project.elice2.productimage.dto.RequestProductImgDto;
import com.project.elice2.productimage.service.ProductImgService;
import com.project.elice2.puductoption.dto.RequestOptionDto;
import com.project.elice2.puductoption.service.ProductOptionService;
import com.project.elice2.users.domain.Users;


@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileUploadUtil fileUploadUtil;
	private final ProductOptionService productOptionService;
	private final ProductImgService productImgService;

    public ProductService(ProductRepository productRepository, 
                          CategoryRepository categoryRepository, 
                          FileUploadUtil fileUploadUtil,
                      	  ProductOptionService productOptionService,
                      	  ProductImgService productImgService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileUploadUtil = fileUploadUtil;
		this.productOptionService = productOptionService;
		this.productImgService = productImgService;
    }

    // 상품 페이징 조회
    private Page<ResponseProductDto> fetchProducts(Page<Product> products) {
        //return products.map(Product::toResponseDto);
        return products.map(product -> ResponseProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .detail(product.getDetail())
                .reqImage(fileUploadUtil.getS3(product.getReqImage()))
                .viewCount(product.getViewCount())
                .price(product.getPrice())
                .count(product.getCount())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .petType(product.getCategory().getPetType().name())
                .build());
    }

    // Pageable 생성
    private Pageable createPageable(int page, int size, Sort.Direction direction, String sortProperty) {
        return PageRequest.of(page, size, Sort.by(direction, sortProperty));
    }

    // 모든 상품 조회(id 내림차순 정렬)
    @Transactional(readOnly = true)
    public Page<ResponseProductDto> findAllProducts(int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.Direction.DESC, "id");
        
        return fetchProducts(productRepository.findAllByAtDeleteIsNull(pageable));
    }
    
    
    // 모든 상품 조회
    @Transactional(readOnly = true)
    public Page<ResponseProductDto> findAllProductsBySort(String sortProperty, Sort.Direction direction, int page, int size) {
        Pageable pageable = createPageable(page, size, direction, sortProperty);
        
        return fetchProducts(productRepository.findAllByAtDeleteIsNull(pageable));
    }

    // 검색 조회
    @Transactional(readOnly = true)
    public Page<ResponseProductDto> searchProducts(String keyword, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.Direction.DESC, "id");
        
        return fetchProducts(productRepository.findAllByAtDeleteIsNullAndTitleContainingIgnoreCase(keyword, pageable));
    }

    // 정렬별 상품 조회 (신상품, 인기상품, 낮은가격, 높은 가격)
    @Transactional(readOnly = true)
    public Page<ResponseProductDto> getProductsBySort(Long categoryId, Sort.Direction direction, String sortProperty, int page, int size) {
        Pageable pageable = createPageable(page, size, direction, sortProperty);
        
        return fetchProducts(productRepository.findByCategoryIdAndAtDeleteIsNull(categoryId, pageable));
    }

    
    // 펫타입별 상품 조회
    @Transactional(readOnly = true)
    public Page<ResponseProductDto> findProductsBypetTypeAndSort(PetType petType, Sort.Direction direction, String sortProperty, int page, int size) {
        Pageable pageable = createPageable(page, size, direction, sortProperty);
        
        return fetchProducts(productRepository.findByCategoryPetTypeAndAtDeleteIsNull(petType, pageable));
    }
    
    // 카테고리별 상품 조회
    @Transactional(readOnly = true)
    public Page<ResponseProductDto> findProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = createPageable(page, size, Sort.Direction.DESC, "id");
        
        return fetchProducts(productRepository.findByCategoryIdAndAtDeleteIsNull(categoryId, pageable));
    }

    // 상품 단일 조회
    @Transactional(readOnly = true)
    public ResponseProductDto findProduct(Long id) {
        Product product = productRepository.findByIdAndAtDeleteIsNull(id).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + id));
        
        //return product.toResponseDto();
        return ResponseProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .detail(product.getDetail())
                .reqImage(fileUploadUtil.getS3(product.getReqImage()))
                .viewCount(product.getViewCount())
                .price(product.getPrice())
                .count(product.getCount())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .petType(product.getCategory().getPetType().name())
                .build();
    }

   
    // 상품 등록
    @Transactional
    public ResponseProductDto addProduct(RequestProductDto requestProductDto, 
    									 Long categoryId,
    									 Users users, 
    									 RequestProductImgDto requestProductImgDtos, 
    									 List<RequestOptionDto> requestOptionDtos) throws IOException {

		// 상품 대표 이미지 저장
		if(requestProductDto.getFile() != null && !requestProductDto.getFile().isEmpty()) {
			uploadFile(requestProductDto, requestProductDto.getFile());
		}

		Category category = categoryRepository.findByIdAndAtDeleteIsNull(categoryId).orElseThrow(() -> new NoSuchElementException("Not Found category id: " + categoryId));

		Product product = requestProductDto.toProduct();
		product.setCategory(category);
		product.setUsers(users);
		

		Product savedProduct = productRepository.save(product);  


		// 디테일 사진 저장    			
		if (requestProductImgDtos.getDetailFile()  != null && !requestProductImgDtos.getDetailFile().isEmpty()) {
			List<RequestProductImgDto> imgDtos = new ArrayList<>();

			for (MultipartFile file : requestProductImgDtos.getDetailFile()) {
				if(!file.isEmpty()) {
					RequestProductImgDto imgDto = new RequestProductImgDto();

					productImgService.uploadFile(imgDto, file);
					imgDtos.add(imgDto);
				}
			}

			for(RequestProductImgDto imgDto : imgDtos) {
				productImgService.addProductImg(imgDto, product.getId());
			}
		}


		// 옵션 사항 저장
		if(!requestOptionDtos.isEmpty() && requestOptionDtos!= null) {
			for(RequestOptionDto requestOptionDto : requestOptionDtos) {
				productOptionService.addOption(requestOptionDto, product.getId());
			}
		}       
        return savedProduct.toResponseDto();
    }
    
    
    // 상품 수정
    @Transactional
    public ResponseProductDto updateProduct(Long id, RequestProductDto requestDto) {
        Product product = productRepository.findByIdAndAtDeleteIsNull(id).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + id));

        Optional.ofNullable(requestDto.getTitle()).ifPresent(product::setTitle);
        Optional.ofNullable(requestDto.getDetail()).ifPresent(product::setDetail);
        Optional.ofNullable(requestDto.getReqImage()).ifPresent(product::setReqImage);
        Optional.ofNullable(requestDto.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(requestDto.getCount()).ifPresent(product::setCount);

        return productRepository.save(product).toResponseDto();
    }

    // 카테고리 수정
    @Transactional
    public void updateCategory(Long productId, Long categoryId) {
        Product product = productRepository.findByIdAndAtDeleteIsNull(productId).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + productId));

        Category category = categoryRepository.findByIdAndAtDeleteIsNull(categoryId).orElseThrow(() -> new NoSuchElementException("Not Found category id: " + categoryId));

        product.setCategory(category);
    }

    // 상품 삭제 (논리 삭제)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndAtDeleteIsNull(id).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + id));
        
        product.setAtDelete(LocalDateTime.now());
        productRepository.save(product);
    }

    // 이미지 파일 업로드
    public void uploadFile(RequestProductDto requestDto, MultipartFile file) throws IOException {
        fileUploadUtil.upload(requestDto, file);
    }
 
	// 실제 이미지 파일 삭제
	@Transactional
	public void deleteImageFile(Long id) throws IOException {	
		Product product = productRepository.findByIdAndAtDeleteIsNull(id).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + id));
		
		fileUploadUtil.deleteS3(product.getReqImage());
	}
    
    
    
    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long id) {
        Product product = productRepository.findByIdAndAtDeleteIsNull(id).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + id));
        
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }
}
