package com.project.elice2.orders.pay;

import com.project.elice2.orders.dto.request.RequestOrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCardPayDto {
    private String rspUid;
    private RequestOrderDto requestOrderDto;
}
