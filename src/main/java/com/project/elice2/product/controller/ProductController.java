package com.project.elice2.product.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.elice2.category.domain.PetType;
import com.project.elice2.category.service.CategoryService;
import com.project.elice2.comment.domain.CommentResponseDto;
import com.project.elice2.comment.service.CommentService;
import com.project.elice2.orders.service.OrdersService;
import com.project.elice2.product.dto.RequestProductDto;
import com.project.elice2.product.dto.RequestProductFullDto;
import com.project.elice2.product.dto.ResponseProductDto;
import com.project.elice2.product.service.ProductService;
import com.project.elice2.productimage.dto.RequestProductImgDto;
import com.project.elice2.productimage.service.ProductImgService;
import com.project.elice2.puductoption.dto.RequestOptionDto;
import com.project.elice2.puductoption.service.ProductOptionService;
import com.project.elice2.review.domain.ReviewResponseDto;
import com.project.elice2.review.service.ReviewService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class ProductController {
	private final ProductService productService;
	private final CategoryService categoryService;
	private final ProductOptionService productOptionService;
	private final ProductImgService productImgService;
	private final ReviewService reviewService;
	private final CommentService commentService;
	private final OrdersService ordersService;


	public ProductController(ProductService productService,
							 CategoryService categoryService,
							 ProductOptionService productOptionService,
							 ProductImgService productImgService,
							 ReviewService reviewService,
							 CommentService commentService,
							 OrdersService ordersService) {
		this.productService =productService;
		this.categoryService = categoryService;
		this.productOptionService = productOptionService;
		this.productImgService = productImgService;
		this.reviewService = reviewService;
		this.commentService = commentService;
		this.ordersService = ordersService;
	}


	// 관리자 페이지 - 상품 관리 페이지
	@GetMapping("/admin/product")
	public String ProductsPage(Model model,
							   @RequestParam(defaultValue = "0") int page,
							   @RequestParam(defaultValue = "10") int size,
	                           @RequestParam(required = false) String petType,
	                           @RequestParam(required = false) Long categoryId) {
		
		Page<ResponseProductDto> products;		
		
		// 상품 분류 조회
		if(petType != null && !petType.isEmpty()) {
			// 펫타입&카테고리 모두 선택
			if(categoryId != null) {
				products = productService.findProductsByCategory(categoryId, page, size);

				model.addAttribute("selectedCategoryId", categoryId);
				 
			} else {
				// 펫타입만 선택
				products = productService.findProductsBypetTypeAndSort(PetType.valueOf(petType), getSortDirection("id"), getSortField("id"), page, size);
			}
			model.addAttribute("selectedPetType", petType);

		} else {
			// 기본 화면
			products = productService.findAllProducts(page, size);	
		}
		
		model.addAttribute("allProducts", products);
		model.addAttribute("dogCategories", categoryService.findCategoryByType(PetType.DOG));
		model.addAttribute("catCategories", categoryService.findCategoryByType(PetType.CAT));

		return "product/product-home";
	}


	// 메인 페이지
	@GetMapping("/product/home")
	public String ProductMainPage(Model model,
								  @RequestParam(defaultValue = "0") int page,
								  @RequestParam(defaultValue = "10") int size,
								  @CurrentUser Users user) { //이메일 인증 경고창

		model.addAttribute("allProducts", productService.findAllProducts(page, size));
		model.addAttribute("newProducts", productService.findAllProductsBySort(getSortField("new"), getSortDirection("new"), 0, 10));
		model.addAttribute("popularProducts", productService.findAllProductsBySort(getSortField("popular"), getSortDirection("popular"), 0, 10));

		//이메일 인증 경고창
		if(user != null) {
			model.addAttribute("user", user);
		}

		return "product/product-main";
	}


	// 더보기 페이지(신상품, 인기상품)
	@GetMapping("/product/home/list")
	public String ProductNewListPage(Model model,
									 @RequestParam(defaultValue = "0") int page,
									 @RequestParam(defaultValue = "6") int size,
									 @RequestParam String sort) {

		model.addAttribute("listProducts", productService.findAllProductsBySort(getSortField(sort), getSortDirection(sort), page, size));
		model.addAttribute("selectedSort", sort);

		return "product/product-main-list";
	}

	// DOG 상품 리스트 조회
	@GetMapping("/product/dog")
	public String getDogProducts(Model model,
								 @RequestParam(required = false) Long categoryId,
								 @RequestParam(defaultValue = "0") int page,
								 @RequestParam(defaultValue = "6") int size,
								 @RequestParam(defaultValue = "new") String sort) {
		PetType petType = PetType.DOG;


		if (categoryId != null) {
			// 카테고리 선택 시
			model.addAttribute("dogProducts", productService.getProductsBySort(categoryId, getSortDirection(sort), getSortField(sort), page, size));
		} else {
			// 카테고리 선택하지 않음
			model.addAttribute("dogProducts", productService.findProductsBypetTypeAndSort(petType, getSortDirection(sort), getSortField(sort), page, size));
		}

		model.addAttribute("dogCategories", categoryService.findCategoryByType(petType));
		model.addAttribute("selectedSort", sort);
		model.addAttribute("selectedCategoryId", categoryId);

		return "product/product-dog-main";
	}

	// CAT 상품 리스트 조회
	@GetMapping("/product/cat")
	public String getCatProducts(Model model,
								 @RequestParam(required = false) Long categoryId,
								 @RequestParam(defaultValue = "0") int page,
								 @RequestParam(defaultValue = "6") int size,
								 @RequestParam(defaultValue = "new") String sort) {
		PetType petType = PetType.CAT;


		if (categoryId != null) {
			// 카테고리 선택 시
			model.addAttribute("catProducts", productService.getProductsBySort(categoryId, getSortDirection(sort), getSortField(sort), page, size));
		} else {
			// 카테고리 선택하지 않음
			model.addAttribute("catProducts", productService.findProductsBypetTypeAndSort(petType, getSortDirection(sort), getSortField(sort), page, size));
		}

		model.addAttribute("catCategories", categoryService.findCategoryByType(petType));
		model.addAttribute("selectedSort", sort);
		model.addAttribute("selectedCategoryId", categoryId);


		return "product/product-cat-main";
	}

	// 정렬 기준 필드
	private String getSortField(String sort) {
		switch (sort) {
			case "popular": return "viewCount";
			case "highPrice": return "price";
			case "lowPrice": return "price";
			case "id" : return "id";
			case "new":
			default: return "atCreate";
		}
	}

	// 정렬 방향
	private Sort.Direction getSortDirection(String sort) {
		return "lowPrice".equals(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
	}

	// 단일 상품 조회
	// 상세 페이지에서 사용될 예정
	@GetMapping("/product/{id}")
	public String getProduct(@PathVariable Long id,
							 @RequestParam(defaultValue = "1") int reviewPage, @RequestParam(defaultValue = "3") int reviewSize,
							 @RequestParam(defaultValue = "1") int commentPage, @RequestParam(defaultValue = "3") int commentSize,
							 Model model,
							 HttpServletRequest request,
							 @CurrentUser Users users) {

		model.addAttribute("product", productService.findProduct(id));
		model.addAttribute("options", productOptionService.getOptions(id));
		model.addAttribute("productImgs", productImgService.findProductImages(id));
		model.addAttribute("user", users);

		// 리뷰 페이지네이션
		Page<ReviewResponseDto> reviewResponseDtoPage = reviewService.findByProductId(id, PageRequest.of(reviewPage - 1, reviewSize, Sort.by("atCreate").descending()));
		model.addAttribute("reviews", reviewResponseDtoPage.getContent());
		model.addAttribute("reviewTotalPages", reviewResponseDtoPage.getTotalPages());

		// Q&A 페이지네이션
		Page<CommentResponseDto> commentResponseDtoPage = commentService.findByProductId(id, PageRequest.of(commentPage - 1, commentSize, Sort.by("atCreate").descending()));
		model.addAttribute("comments", commentResponseDtoPage.getContent());
		model.addAttribute("commentTotalPages", commentResponseDtoPage.getTotalPages());


		// 리뷰를 했는지 체크
		model.addAttribute("isReviewer", reviewService.checkReviewWriter(users, id));
		// 상품 구매자인지 체크
		model.addAttribute("isConsumer", ordersService.checkReveiw(users, id));


		// 조회수 증가 로직
		if (users != null) { // 로그인 여부 확인
			String username = users.getUsername();
			HttpSession session = request.getSession();
			String viewKey = "viewedProduct_" + id + "_" + username; // 고유 키 생성

			if (session.getAttribute(viewKey) == null) {
				productService.incrementViewCount(id); // 조회수 증가
				session.setAttribute(viewKey, true); // 세션에 기록
			}
		}

		return "product/product-detail";
	}


	// 상품 검색 조회
	@GetMapping("/product/search")
	public String searchProducts(@RequestParam String keyword,
								 @RequestParam(defaultValue = "0") int page,
								 @RequestParam(defaultValue = "6") int size,
								 Model model) {

		if (keyword == null || keyword.trim().isEmpty()) {
			model.addAttribute("errorMessage", "검색어를 입력해주세요.");
			return "product/product-search";
		}

		model.addAttribute("allProducts", productService.searchProducts(keyword, page, size));
		model.addAttribute("keyword", keyword);

		return "product/product-search";
	}



	// 상품 추가 페이지
	// 관리자 페이지에서 사용
	@GetMapping("/admin/product/create")
	public String createProductPage(Model model) {
		model.addAttribute("dogCategories", categoryService.findCategoryByType(PetType.DOG));
		model.addAttribute("catCategories", categoryService.findCategoryByType(PetType.CAT));
		model.addAttribute("requestProductFullDto", new RequestProductFullDto()); // 임시객체

		return "product/product-create";
	}


	// 상품 추가
	// 관리자 페이지에서 사용
	@PostMapping("/admin/product/create")
	public String createProduct(
			@Valid @ModelAttribute RequestProductFullDto requestProductFullDto,
			BindingResult result,
			Long categoryId,
			Model model,
			@CurrentUser Users users,
			HttpServletResponse response) throws IOException {


		// 유효성 검사
		if (result.hasErrors()) {
			result.getAllErrors().forEach(error -> log.debug("Creating Product : Validation Error: {}", error.toString()));
			
		    Map<String, String> fieldErrors = result.getFieldErrors().stream()
	                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));  // 에러 map형태로 반환

			model.addAttribute("dogCategories", categoryService.findCategoryByType(PetType.DOG));
			model.addAttribute("catCategories", categoryService.findCategoryByType(PetType.CAT));
			model.addAttribute("requestProductFullDto", requestProductFullDto);
			
			model.addAttribute("fieldErrors", fieldErrors);
			
	        // HTTP 상태 코드 400으로 응답
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			return "product/product-create";
		}


		RequestProductDto requestProductDto =  requestProductFullDto.getProduct();
		RequestProductImgDto requestProductImgDtos = requestProductFullDto.getProductImages();
		List<RequestOptionDto> requestOptionDtos = requestProductFullDto.getOptions();

		ResponseProductDto addProduct = productService.addProduct(requestProductDto, categoryId, users, requestProductImgDtos, requestOptionDtos);
		
		return "redirect:/admin/product";
	}



	// 상품 수정 페이지
	// 관리자 페이지에서 사용
	@GetMapping("/admin/product/update/{id}")
	public String updateProductPage(@PathVariable Long id, Model model) {
		model.addAttribute("product", productService.findProduct(id));
		model.addAttribute("dogCategories", categoryService.findCategoryByType(PetType.DOG));
		model.addAttribute("catCategories", categoryService.findCategoryByType(PetType.CAT));

		model.addAttribute("requestDto", new RequestProductDto()); // 임시객체

		return "product/product-update";
	}

	// 상품 수정(product option, product image는 각각 수정 페이지 만듬)
	// 관리자 페이지에서 사용
	@PostMapping("/admin/product/update")
	public String updateProduct(@Valid @ModelAttribute RequestProductDto requestDto,
								BindingResult result,
								Long id,
								Long categoryId,
								Model model,
								HttpServletResponse response) throws IOException {

		if(result.hasErrors()) {
			result.getAllErrors().forEach(error -> log.debug("Updating Product id {} : Validation Error: {}", id, error.toString()));
			
		    Map<String, String> fieldErrors = result.getFieldErrors().stream()
		                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));  // 에러 map형태로 반환

			
			model.addAttribute("product", productService.findProduct(id));
			model.addAttribute("dogCategories", categoryService.findCategoryByType(PetType.DOG));
			model.addAttribute("catCategories", categoryService.findCategoryByType(PetType.CAT));
			
			model.addAttribute("requestDto", requestDto);
			model.addAttribute("fieldErrors", fieldErrors);
			
	        // HTTP 상태 코드 400으로 응답
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			return "product/product-update";
		}

		// 사진 업로드
		if(requestDto.getFile() != null && !requestDto.getFile().isEmpty()) {
			productService.uploadFile(requestDto, requestDto.getFile());

			// 기존 저장된 사진은 삭제
			productService.deleteImageFile(id);
		}
		
		productService.updateCategory(id, categoryId);
		productService.updateProduct(id, requestDto);

		return "redirect:/admin/product"; // 관리자 페이지로 이동
	}

	// 상품 삭제
	// 관리자 페이지에서 사용
	@PostMapping("/admin/product/delete/{id}")
	public String deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);

		return "redirect:/admin/product"; // 관리자 페이지로 이동
	}
}
