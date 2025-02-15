package com.project.elice2.deliveryaddress.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {

    private Long deliveryAddressId;

    @NotEmpty(message = "받는 사람을 입력해 주세요.")
    private String recipient;

    @NotEmpty(message = "주소를 입력해 주세요.")
    private String address;

    @NotEmpty(message = "주소를 입력해 주세요.")
    private String detailAddress;

    @NotEmpty(message = "전화번호를 입력해 주세요.")
    private String phone;

    private String request;

}
