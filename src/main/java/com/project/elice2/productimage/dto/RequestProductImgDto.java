package com.project.elice2.productimage.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.project.elice2.fileUpload.FileUpload;
import com.project.elice2.fileUpload.ValidFileList;
import com.project.elice2.productimage.domain.ProductImage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestProductImgDto implements FileUpload {
	
	@ValidFileList(message = "파일은 5MB 이하, 파일 형식은 jpg, png만 가능합니다.")
    private List<MultipartFile> detailFile;
	
	private String url;
    
	
    public ProductImage toProductImage() {
    	return ProductImage.builder()
    			.url(url)
    			.build();
    }

	@Override
	public void setImagePath(String imagePath) {
		this.url = imagePath;
	}
}
