package com.project.elice2.users.dto;

import com.project.elice2.users.validation.PasswordConfirmCheck;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordConfirmCheck(text1 = "password", text2 = "passwordConfirm")
public class UpdatePasswordRequest {

    @NotBlank
    @Length(min = 8, max = 25)
    private String password;

    @NotBlank
    @Length(min = 8, max = 25)
    private String passwordConfirm;
}
