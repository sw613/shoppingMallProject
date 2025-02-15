package com.project.elice2.deliveryaddress.domain;

import com.project.elice2.deliveryaddress.dto.UpdateAddressRequest;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_address_id")
    private Long id;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    @Pattern(regexp = "010-\\d{4}-\\d{4}", message = "전화번호는 010-0000-0000 형식이어야 합니다.")
    private String phone;

    private String request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
