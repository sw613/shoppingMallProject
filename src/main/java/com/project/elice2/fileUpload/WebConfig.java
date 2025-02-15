package com.project.elice2.fileUpload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//	// 경로는 properties에서 설정
//	@Value("${file.upload.path}") 
//	String basePath;
//	
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/files/**")  // view에서 접근할 경로
//				.addResourceLocations("file:///" + basePath); // 실제 파일 저장 경로
//	}
}
