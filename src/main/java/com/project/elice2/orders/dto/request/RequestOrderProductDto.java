package com.project.elice2.orders.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderProductDto {

    @NotNull
    private Long optionId;

    @Positive
    private Long count;

}
