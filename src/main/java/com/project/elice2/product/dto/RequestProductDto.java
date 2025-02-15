package com.project.elice2.product.dto;

import org.springframework.web.multipart.MultipartFile;

import com.project.elice2.fileUpload.FileUpload;
import com.project.elice2.fileUpload.ValidFile;
import com.project.elice2.product.domain.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestProductDto implements FileUpload {
	@NotBlank(message = "값을 입력해 주세요.")
	private String title;
	
	@NotBlank(message = "값을 입력해 주세요.")
    private String detail;
	
	@ValidFile(message = "파일은 5MB이하, 파일 형식은 jpg, png만 가능합니다.")
    private MultipartFile file;
    private String reqImage;
    
    @NotNull(message = "가격은 비어있으면 안됩니다.")
    @PositiveOrZero(message = "양수로 입력해 주세요.")
    private Long price;
    
    @NotNull(message = "수량은 비어있으면 안됩니다.")
    @PositiveOrZero(message = "양수로 입력해 주세요.")
    private Long count;
    
    public Product toProduct() {

    	return Product.builder()
	    			.title(title)
	    			.detail(detail)
	    			.reqImage(reqImage)
	    			.price(price)
	    			.count(count)
	    			.build();
	 }

	@Override
	public void setImagePath(String imagePath) {
		this.reqImage = imagePath;		
	}
}
