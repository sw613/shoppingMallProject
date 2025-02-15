package com.project.elice2.orders.pay;

import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.dto.request.RequestOrderDto;
import com.project.elice2.orders.service.OrdersService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/kakaopay")
@RequiredArgsConstructor
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    @PostMapping("/ready")
    public KakaoReadyResponse readyToKakaoPay(@RequestBody RequestPayDto requestPayDto){
        System.out.println("ready요청 컨트롤러 완료");
        return kakaoPayService.kakaoPayReady(requestPayDto);
    }

    @PostMapping("/success")
    public ResponseEntity<KakaoApproveResponse> afterPayRequest(@CurrentUser Users users, @RequestParam("pg_token") String pgToken, @RequestBody @Validated RequestOrderDto requestOrderDto){
        log.info("pgtoken: " + pgToken );
        log.info("orderDto: " + requestOrderDto);
        KakaoApproveResponse kakaoApprove = kakaoPayService.approveResponse(users, pgToken, requestOrderDto);

        return ResponseEntity.ok(kakaoApprove);
    }

    @GetMapping("/cancel")
    public void cancel() {
        throw new RuntimeException("결제가 취소되었습니다.");
    }

    @GetMapping("/fail")
    public void fail() {
        throw new IllegalArgumentException("결제가 실패했습니다.");
    }

}
