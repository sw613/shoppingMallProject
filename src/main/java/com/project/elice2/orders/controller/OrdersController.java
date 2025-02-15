package com.project.elice2.orders.controller;

import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.domain.PayMethod;
import com.project.elice2.orders.dto.request.RequestRefundDto;
import com.project.elice2.orders.dto.response.ResponseOrderSuccessDto;
import com.project.elice2.orders.pay.KakaoCancelResponse;
import com.project.elice2.orders.pay.KakaoPayService;
import com.project.elice2.orders.pay.PaymentService;
import com.project.elice2.orders.service.OrdersService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService ordersService;

    private final KakaoPayService kakaoPayService;

    private final PaymentService paymentService;

    //사용자의 주문 리스트
   /* @GetMapping()
    public ResponseEntity<Page<ResponseOrderDto>> viewOrdersAll(@CurrentUser Users user){
        return ResponseEntity.ok(ordersService.findAllOrders(user));
    }*/

    //주문 항목 보기
   /* @GetMapping("/view/{orderId}")
    public ResponseEntity<List<ResponseOrderProductDto>> viewOrderProductList(@PathVariable Long orderId){
        return ResponseEntity.ok(ordersService.findOrdersProductAll(orderId));
    }*/

    //주문 완료시 주문 정보
    @GetMapping("/success/{orderId}")
    public ResponseEntity<ResponseOrderSuccessDto> getOrderSuccess(@CurrentUser Users user, @PathVariable Long orderId){
        return ResponseEntity.ok(ordersService.orderSuccess(user, orderId));
    }

    // 주문 취소
    // 사용 불확실
    /*
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Boolean> deleteOrderProductList(@PathVariable Long orderId){
        return ResponseEntity.ok(ordersService.deleteOrder(orderId));
    }*/

    @DeleteMapping("/view/{orderId}/delete/{deleteId}")
    public ResponseEntity<Boolean> deleteOrderProduct(@PathVariable Long orderId ,@PathVariable Long deleteId){
        boolean b = ordersService.deleteOrderProduct(orderId,deleteId);
        if(b){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.badRequest().body(false);
        }
    }

    @Operation(summary = "환불경로", description = "환불")
    @PostMapping("/refund")
    public ResponseEntity<Boolean> refund(@CurrentUser Users users, @RequestParam("ordersId") Long ordersId) throws IOException {
        Orders orders = ordersService.findByOrdersId(ordersId);
        if(!orders.getUsers().equals(users)){
            throw new RuntimeException("주문정보와 회원정보가 일치하지 않습니다.");
        }
        if(orders.getPayMethod().equals(PayMethod.카카오페이)) {
            KakaoCancelResponse kakaoCancelResponse = kakaoPayService.kakaoCancel(orders);
            return ResponseEntity.ok(true);
        }else{
            paymentService.refund(orders);
            return ResponseEntity.ok(true);
        }
    }

    @Operation(summary = "선택 환불 경로", description = "선택 환불")
    @PostMapping("/selectRefund")
    public ResponseEntity<Boolean> selectRefund(@CurrentUser Users users, @RequestBody RequestRefundDto requestRefundDto) throws IOException {
        Orders orders = ordersService.findByOrdersId(requestRefundDto.getOrderId());
        if(!orders.getUsers().equals(users)){
            throw new RuntimeException("주문정보와 회원정보가 일치하지 않습니다.");
        }

        if(orders.getPayMethod().equals(PayMethod.카카오페이)) {
            KakaoCancelResponse kakaoCancelResponse = kakaoPayService.kakaoSelectCancel(orders, requestRefundDto.getRefundOptions());
            return ResponseEntity.ok(true);
        }else{
            paymentService.selectRefund(orders, requestRefundDto.getRefundOptions());
            return ResponseEntity.ok(true);
        }
    }
}
