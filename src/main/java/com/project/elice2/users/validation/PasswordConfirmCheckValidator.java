package com.project.elice2.users.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class PasswordConfirmCheckValidator implements ConstraintValidator<PasswordConfirmCheck, Object> {

    private String message;
    private String text1;
    private String text2;

    @Override
    public void initialize(PasswordConfirmCheck constraintAnnotation) {
        message = constraintAnnotation.message();
        text1 = constraintAnnotation.text1();
        text2 = constraintAnnotation.text2();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        String password = getFieldValue(object, text1);
        String confirm = getFieldValue(object, text2);

        if (password == null || confirm == null) {
            return true; // 필드 값이 null인 경우 NotEmpty에서 이미 처리하므로 true 반환
        }

        if (!password.equals(confirm)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(text2) // 에러를 confirmPassword 필드에 바인딩
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    // 리플렉션을 이용하여 필드를 가져오는 부분
    private String getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(object);
            return value != null ? value.toString() : null; // 필드 값이 null인 경우 null 반환
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Field access error: {}", fieldName, e);
            return null; // 필드를 찾지 못해도 null 반환
        }
    }
}