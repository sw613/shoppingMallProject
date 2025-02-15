package com.project.elice2.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderSuccessDto {
    private LocalDateTime buyDate;

    private Long quantity;

    private Long totalMoney;

    private String address;
}
