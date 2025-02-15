package com.project.elice2.orderproduct.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDeliveryDto {
    @NotNull
    private Long orderProductId;

    @NotBlank
    private String isState;
}
