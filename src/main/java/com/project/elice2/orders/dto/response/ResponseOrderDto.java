package com.project.elice2.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderDto {
    private Long id;
    private Long totalMoney;
    private LocalDateTime buyDate;
    private String address;
    private boolean state;
}
