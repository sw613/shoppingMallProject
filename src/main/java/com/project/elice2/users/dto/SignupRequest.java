package com.project.elice2.users.dto;

import com.project.elice2.users.validation.PasswordConfirmCheck;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordConfirmCheck(text1 = "password", text2 = "passwordConfirm")
public class SignupRequest {

    @NotBlank(message = "닉네임을 입력하세요.")
    @Length(min = 3, max = 10, message = "3자 이상 10자 이하로 입력하세요.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]{3,10}$", message = "한글, 영문, 숫자만 사용할 수 있습니다.")
    private String username;

    @Email(message = "올바른 이메일 형식을 입력하세요.")
    @NotBlank(message = "이메일을 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Length(min = 8, max = 25, message = "8자 이상 25자 이하로 입력하세요.")
    private String password;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Length(min = 8, max = 25, message = "8자 이상 25자 이하로 입력하세요.")
    private String passwordConfirm;

//    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
//    public boolean isPasswordMatching() {
//        // 둘 다 비어 있지 않은 경우에만 비교
//        if (password == null || confirmPassword == null) {
//            return true; // NotEmpty에서 처리하므로 여기서는 통과시킴
//        }
//        return password.equals(confirmPassword);
//    }
}
