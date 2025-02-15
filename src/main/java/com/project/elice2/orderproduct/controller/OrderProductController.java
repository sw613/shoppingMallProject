package com.project.elice2.orderproduct.controller;

import com.project.elice2.orderproduct.dto.request.RequestDeliveryDto;
import com.project.elice2.orderproduct.service.OrderProductService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/product")
@RequiredArgsConstructor
public class OrderProductController {
    private final OrderProductService orderProductService;

    @PutMapping("/stateEdit")
    public ResponseEntity<Boolean> orderStateUpdate(@CurrentUser Users user, @RequestBody @Validated RequestDeliveryDto requestDeliveryDto){
        boolean b = orderProductService.updateDeliveryUpdate(user, requestDeliveryDto);
        if(b){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.badRequest().body(false);
        }
    }


}
