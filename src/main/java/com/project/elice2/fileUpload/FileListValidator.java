package com.project.elice2.fileUpload;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class FileListValidator implements ConstraintValidator<ValidFileList, List <MultipartFile>> {

    private long maxSize;
    private String[] allowedTypes;

    
    public void initialize(ValidFileList constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedTypes = constraintAnnotation.allowedTypes();
    }

    
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return false;
        }
        
        for(MultipartFile file : files) {
        
        	if (file.getSize() > maxSize) {
        		context.disableDefaultConstraintViolation();
        		context.buildConstraintViolationWithTemplate("파일 크기는 " + (maxSize / (1024 * 1024)) + "MB 이하만 가능합니다.")
                    	.addConstraintViolation();
        		return false;
        	}
        
        	String contentType = file.getContentType();
        	if (contentType == null || !isAllowedType(contentType)) {
        		context.disableDefaultConstraintViolation();
        		context.buildConstraintViolationWithTemplate("파일 형식은 " + String.join(", ", allowedTypes) + "만 가능합니다.")
        				.addConstraintViolation();
        		return false;
        	}
        }    
        return true;
    }
    

    private boolean isAllowedType(String contentType) {
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }    
    
}
