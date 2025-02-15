package com.project.elice2.users.dto;

import com.project.elice2.users.domain.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UpdateUserInfoRequest {

    private String email;

    @NotBlank(message = "닉네임을 입력하세요.")
    @Length(min = 3, max = 10, message = "3자 이상 10자 이하로 입력하세요.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]{3,10}$", message = "한글, 영문, 숫자만 사용할 수 있습니다.")
    private String username;

    private String phoneNumber;

    private boolean emailVerified;

    private LocalDateTime atCreate;


    public UpdateUserInfoRequest(Users user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.emailVerified = user.isEmailVerified();
        this.atCreate = user.getAtCreate();
    }
}
