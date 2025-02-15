package com.project.elice2.deliveryaddress.controller.test;

import com.project.elice2.deliveryaddress.domain.DeliveryAddress;
import com.project.elice2.deliveryaddress.dto.*;
import com.project.elice2.deliveryaddress.service.DeliveryAddressService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeliveryAddressRestController {

    private final DeliveryAddressService deliveryAddressService;

    //TODO: 주소 생성 5개 제한 추가??
    @PostMapping("/address/create")
    public ResponseEntity<DeliveryAddressDto> create(@RequestBody CreateRequest request) {

        //TODO: 수정하기
        Long userId = 1L;
        DeliveryAddressDto deliveryAddressDto = deliveryAddressService.create(request, userId);

        return ResponseEntity.ok(deliveryAddressDto);
    }

    @DeleteMapping("/address/delete")
    public ResponseEntity<String> delete(@Valid @RequestBody DeleteRequest request) {

        System.out.println("deliveryAddressId: " + request.getDeliveryAddressId());

        deliveryAddressService.delete(request);
        return ResponseEntity.ok("delete success");
    }

    @PutMapping("/address/update")
    public ResponseEntity<String> update(@Valid @RequestBody UpdateRequest request) {

        deliveryAddressService.update(request);
        return ResponseEntity.ok("update success");
    }

    @GetMapping("/address/list")
    public Result getAll() {

        List<DeliveryAddress> findAddresses = deliveryAddressService.findAll();

        List<DeliveryAddressDto> collect = findAddresses.stream()
                .map(a -> new DeliveryAddressDto(a.getId(), a.getRecipient(), a.getAddress(), a.getDetailAddress(), a.getPhone(), a.getRequest(), a.getUser().getId()))
                .toList();

        return new Result(collect);
    }

    @GetMapping("/address/list/dto")
    public Result getAllByUserId(@RequestParam Long userId, @CurrentUser Users user) {
        List<AddressDto> byUserIdWithDto = deliveryAddressService.findByUserId_withDto(user);
        return new Result(byUserIdWithDto);
    }


    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
