package com.project.elice2.deliveryaddress.dto;

import com.project.elice2.deliveryaddress.domain.DeliveryAddress;
import com.project.elice2.users.domain.Users;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateAddressRequest {

    private Long id;

    @NotBlank
    private String addressName;

    @NotBlank
    private String recipient;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String address;

    @NotBlank
    private String detailAddress;

    @NotBlank
    private String phone;

    private String request;

    public DeliveryAddress toDeliveryAddress(Users user) {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setAddressName(addressName);
        deliveryAddress.setRecipient(recipient);
        deliveryAddress.setZipCode(zipCode);
        deliveryAddress.setAddress(address);
        deliveryAddress.setDetailAddress(detailAddress);
        deliveryAddress.setPhone(phone);
        deliveryAddress.setRequest(request);
        deliveryAddress.setUser(user);
        return deliveryAddress;
    }
}
