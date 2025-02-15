package com.project.elice2.users.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotEmpty(message = "회원 이름을 입력해 주세요.")
    private String username;
}
