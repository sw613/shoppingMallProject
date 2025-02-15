package com.project.elice2.deliveryaddress.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRequest {

    @NotNull(message = "deliveryAddressId는 필수입니다.")
    private Long deliveryAddressId;
}
