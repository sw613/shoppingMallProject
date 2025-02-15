package com.project.elice2.users.validation;

import com.project.elice2.users.dto.SignupRequest;
import com.project.elice2.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignupValidator implements Validator {

    private final UserRepository userRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return SignupRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupRequest request = (SignupRequest) target;

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.rejectValue("email", "email.exists", new Object[]{request.getEmail()}, "이미 사용중인 이메일입니다.");
        }
    }
}
