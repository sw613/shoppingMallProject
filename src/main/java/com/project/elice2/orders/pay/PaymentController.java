package com.project.elice2.orders.pay;

import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.domain.PayMethod;
import com.project.elice2.orders.service.OrdersService;
import com.project.elice2.users.domain.Authority;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/iamport")
public class PaymentController {

    private final PaymentService paymentService;

    private final KakaoPayService kakaoPayService;
    private IamportClient iamportClient;

    private final OrdersService ordersService;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init(){
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @PostMapping("/verify")
    public ResponseEntity<Long> verifyPayment(@CurrentUser Users users, @RequestBody RequestCardPayDto requestCardPayDto) throws IOException {
        String impUid = requestCardPayDto.getRspUid();
        System.out.println("받는 데이터 :" + requestCardPayDto);
        try {
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
            Payment payment = response.getResponse();
            System.out.println("payment tid: " + payment.getPgTid());
            System.out.println("payment state: " + payment.getStatus());

            if ("paid".equals(payment.getStatus())) {
                Long num = ordersService.cteateOrder(users, requestCardPayDto.getRequestOrderDto(), payment.getPgTid(), payment.getMerchantUid());
                return ResponseEntity.ok(num);
            } else {
                return ResponseEntity.badRequest().body(0L);
            }
        } catch (RuntimeException e) {
            log.info("주문 상품 환불 진행");
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
            Payment payment = response.getResponse();
            String token = paymentService.getToken(apiKey, secretKey);
            paymentService.refundRequest(token, payment.getMerchantUid(), e.getMessage());
            return ResponseEntity.badRequest().body(0L);
        }
    }

}
