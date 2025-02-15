package com.project.elice2.orders.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRefundDto {
    private Long orderId;
    private List<Long> refundOptions;
}
