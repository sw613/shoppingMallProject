package com.project.elice2.deliveryaddress.dto;

import com.project.elice2.deliveryaddress.domain.DeliveryAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private Long id;
    private String addressName;
    private String recipient;
    private String zipCode;
    private String address;
    private String detailAddress;
    private String phone;
    private String request;
//    private Long userId;


    public AddressDto(DeliveryAddress deliveryAddress) {
        this.id = deliveryAddress.getId();
        this.addressName = deliveryAddress.getAddressName();
        this.recipient = deliveryAddress.getRecipient();
        this.zipCode = deliveryAddress.getZipCode();
        this.address = deliveryAddress.getAddress();
        this.detailAddress = deliveryAddress.getDetailAddress();
        this.phone = deliveryAddress.getPhone();
        this.request = deliveryAddress.getRequest();
    }
}
