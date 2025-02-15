package com.project.elice2.fileUpload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class FileUploadUtil {
	
//	// 경로는 properties에서 설정
//	@Value("${file.upload.path}") 
//	String basePath;
//	
//	// 이미지 업로드
//    public void setFileName(FileUpload dto, MultipartFile file) throws IOException {
//
//    	File f = new File(basePath);
//    	if(!f.exists()) {
//    		f.mkdirs();
//    	}
//    	
//    	// 서버 저장용 파일 이름 설정
//    	UUID uuid = UUID.randomUUID();
//    	String fileName = uuid + "_" + file.getOriginalFilename();
//    	
//    	// 해당 경로에 파일 저장
//    	File saveFile = new File(basePath, fileName);
//    	file.transferTo(saveFile);
//    	
//    	// 파일 이름, 경로 저장
//    	dto.setImagePath("/files/" + fileName);
//    }	
//	
//    
//    // 이미지 파일 삭제
//    public void deleteImg(String url) throws IOException {
//    	String imgPath = url.startsWith("/files/") ? url.substring("/files/".length()) : url;
//    	
//    	Path path = Paths.get(basePath, imgPath);
//    	
//    	if(Files.exists(path)) {
//    		Files.delete(path);    		
//    	}
//    }
	
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	
	@Value("${cloud.aws.s3.bucket.url}")
	private String defaultUrl;
	
    @Value("${cloud.aws.s3.path.review}")
    private String reviewPath; // 폴더 경로 (productImg)
	
	private final AmazonConfig amazonConfig;

	
	public String upload(FileUpload dto, MultipartFile uploadFile) throws IOException {
			
    	// 파일 이름 설정
    	UUID uuid = UUID.randomUUID();
    	String fileName = reviewPath + "/" + uuid + "_" + uploadFile.getOriginalFilename();
    	
    	String contentType = uploadFile.getContentType(); // 업로드된 파일의 Content-Type 가져오기
    	
    	// S3에 이미지 파일 업로드
    	amazonConfig.s3Client().putObject(PutObjectRequest.builder()
    				.bucket(bucket)
    				.key(fileName)
    				.contentType(contentType)
    				.build(),
    				RequestBody.fromInputStream(uploadFile.getInputStream(), uploadFile.getSize())
    				);
    	
    	// dto에 url저장
    	dto.setImagePath(fileName);
    	
    	return defaultUrl + "/" + fileName;   // 업로드된 파일의 url 반환
	}
	
	// S3에 올린 이미지 파일 삭제
	public void deleteS3(String fileName) throws IOException {
		amazonConfig.s3Client().deleteObject(DeleteObjectRequest.builder()
						.bucket(bucket)
						.key(fileName)
						.build()
						);
	}
	
	// 업로드된 이미지 url 반환
	public String getS3(String fileName) {
		return defaultUrl + "/" + fileName; 
	}
}
