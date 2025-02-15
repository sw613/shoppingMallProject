package com.project.elice2.deliveryaddress.repository;

import com.project.elice2.deliveryaddress.domain.DeliveryAddress;
import com.project.elice2.deliveryaddress.dto.AddressDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    List<DeliveryAddress> findByUserId(Long userId);

}
