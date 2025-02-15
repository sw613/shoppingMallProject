package com.project.elice2.orders.pay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPayDto {
    private String orderId;
    private Long quantity;
    private Long totalAmount;
}
