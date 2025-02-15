package com.project.elice2.deliveryaddress.service;

import com.project.elice2.deliveryaddress.domain.DeliveryAddress;
import com.project.elice2.deliveryaddress.dto.*;
import com.project.elice2.deliveryaddress.repository.DeliveryAddressRepository;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;


    public List<DeliveryAddress> findByUserId(Users user) {
        return deliveryAddressRepository.findByUserId(user.getId());
    }

    public List<AddressDto> findByUserId_withDto(Users user) {
        List<DeliveryAddress> findAddresses = deliveryAddressRepository.findByUserId(user.getId());

        return findAddresses.stream()
                .map(address -> new AddressDto(address))
                .toList();
    }

    public void createAddress(UpdateAddressRequest request, Users user) {
        DeliveryAddress address = request.toDeliveryAddress(user);
        deliveryAddressRepository.save(address);
    }

    public void updateAddress(Long addressId, UpdateAddressRequest request, Users findUser) {
        DeliveryAddress address = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송지가 존재하지 않습니다. id: " + addressId));
        address.setAddressName(request.getAddressName());
        address.setRecipient(request.getRecipient());
        address.setZipCode(request.getZipCode());
        address.setAddress(request.getAddress());
        address.setDetailAddress(address.getDetailAddress());
        address.setPhone(request.getPhone());
        address.setRequest(request.getRequest());
    }

    public void deleteAddress(Long addressId, Long userId) {
        DeliveryAddress address = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송지가 존재하지 않습니다. id: " + addressId));

        // 삭제 요청한 사용자가 해당 주소의 소유자인지 확인
        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 배송지를 삭제할 권한이 없습니다.");
        }

        deliveryAddressRepository.delete(address);
    }








    public DeliveryAddressDto create(CreateRequest request, Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("해당 회원을 조회할 수 없습니다 : " + userId));

        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setRecipient(request.getRecipient());
        deliveryAddress.setAddress(request.getAddress());
        deliveryAddress.setDetailAddress(request.getDetailAddress());
        deliveryAddress.setPhone(request.getPhone());
        deliveryAddress.setRequest(request.getRequest());
        deliveryAddress.setUser(user);

        deliveryAddressRepository.save(deliveryAddress);

        return new DeliveryAddressDto().toDeliveryAddressDto(deliveryAddress);
    }

    public void delete(DeleteRequest request) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new IllegalArgumentException("삭제할 주소가 존재하지 않습니다: " + request.getDeliveryAddressId()));

        deliveryAddressRepository.deleteById(request.getDeliveryAddressId());
    }

    public void update(UpdateRequest request) {

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new IllegalArgumentException("삭제할 주소가 존재하지 않습니다: " + request.getDeliveryAddressId()));

        deliveryAddress.setRecipient(request.getRecipient());
        deliveryAddress.setAddress(request.getAddress());
        deliveryAddress.setDetailAddress(request.getDetailAddress());
        deliveryAddress.setPhone(request.getPhone());
        deliveryAddress.setRequest(request.getRequest());
    }

    public List<DeliveryAddress> findAll() {
        return deliveryAddressRepository.findAll();
    }



}
