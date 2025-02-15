package com.project.elice2.fileUpload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


@Documented
@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ValidFile {
    String message() default "유효하지 않은 파일입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long maxSize() default 5 * 1024 * 1024;  // 기본 최대 파일 크기: 5MB
    String[] allowedTypes() default { "image/png", "image/jpeg" }; // 허용 파일 형식
}
