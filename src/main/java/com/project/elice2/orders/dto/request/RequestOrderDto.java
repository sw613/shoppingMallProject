package com.project.elice2.orders.dto.request;

import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.domain.PayMethod;
import com.project.elice2.users.domain.Users;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderDto {
    @NotNull
    private String orderId;

    @NotNull
    private String address;

    @NotNull
    private String phone;

    @NotNull
    private String recipient;

    @NotNull
    private String request;

    @NotNull
    private List<RequestOrderProductDto> orderProductDtoList;

    @NotNull
    private String payMethod;

    public Orders toEntity(Users users, String tid){
        return Orders.builder()
                .users(users)
                .tid(tid)
                .requestOrderDto(this)
                .payMethod(this.toEnum())
                .build();
    }

    public PayMethod toEnum(){
        try {
            return PayMethod.valueOf(this.payMethod);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("없는 결제 방법: " + this.payMethod, e);
        }
    }
}
