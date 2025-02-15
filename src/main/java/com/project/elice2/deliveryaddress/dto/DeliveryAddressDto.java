package com.project.elice2.deliveryaddress.dto;

import com.project.elice2.deliveryaddress.domain.DeliveryAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDto {

    private Long id;
    private String recipient;
    private String address;
    private String detailAddress;
    private String phone;
    private String request;
    private Long userId;

    public DeliveryAddressDto toDeliveryAddressDto(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressDto(
                deliveryAddress.getId(),
                deliveryAddress.getRecipient(),
                deliveryAddress.getAddress(),
                deliveryAddress.getDetailAddress(),
                deliveryAddress.getPhone(),
                deliveryAddress.getRequest(),
                deliveryAddress.getUser().getId()
        );
    }
}
