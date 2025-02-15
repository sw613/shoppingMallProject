package com.project.elice2.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResultOptionDto {
    private Long totalPrice;
    private boolean isState;
}
