package com.project.elice2.orderproduct.dto.response;

import com.project.elice2.orderproduct.domain.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOrderProductDto {
    private Long productOrderId;

    private Long productId;

    private String productName;

    private Long price;

    private Long count;

    private String image;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
