package com.project.elice2.orders.pay;

import com.project.elice2.orderproduct.domain.OrderProduct;
import com.project.elice2.orderproduct.domain.OrderStatus;
import com.project.elice2.orderproduct.service.OrderProductService;
import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.dto.request.RequestOrderDto;
import com.project.elice2.orders.service.OrdersService;
import com.project.elice2.puductoption.domain.ProductOption;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KakaoPayService {

    private final KakaoPayProperties payProperties;

    private final OrdersService ordersService;

    private final OrderProductService orderProductService;

    private KakaoReadyResponse kakaoReady;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + payProperties.getSecretKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public KakaoReadyResponse kakaoPayReady(RequestPayDto requestPayDto){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", payProperties.getCid());
        parameters.put("partner_order_id", requestPayDto.getOrderId());
        parameters.put("partner_user_id", "USER12345");
        parameters.put("item_name", "주문");
        parameters.put("quantity", String.valueOf(requestPayDto.getQuantity()));
        parameters.put("total_amount", String.valueOf(requestPayDto.getTotalAmount()));
        parameters.put("vat_amount", "200");
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", "https://elice-happy-shop.duckdns.org/view/payment/success");
        parameters.put("fail_url", "https://elice-happy-shop.duckdns.org/view/payment/fail");
        parameters.put("cancel_url", "https://elice-happy-shop.duckdns.org/api/orders/cancel");
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();

        kakaoReady = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                requestEntity,
                KakaoReadyResponse.class);
        return kakaoReady;
    }
    public KakaoApproveResponse approveResponse (Users user, String pgToken, RequestOrderDto requestOrderDto){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", payProperties.getCid());
        parameters.put("tid", kakaoReady.getTid());
        parameters.put("partner_order_id", requestOrderDto.getOrderId());
        parameters.put("partner_user_id", "USER12345");
        parameters.put("pg_token", pgToken);

        HttpHeaders headers = getHeaders();

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

        RestTemplate restTemplate = new RestTemplate();

        KakaoApproveResponse kakaoApproveResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                requestEntity,
                KakaoApproveResponse.class);
        if(ordersService.cteateOrder(user, requestOrderDto, kakaoReady.getTid(), null)==null){
            log.error("주문 데이터 생성 오류");
        }

        return kakaoApproveResponse;
    }


    @Transactional
    public KakaoCancelResponse kakaoCancel(Orders orders){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", payProperties.getCid());
        parameters.put("tid", orders.getTid());
        parameters.put("cancel_amount", String.valueOf(orders.getTotalMoney()));
        parameters.put("cancel_tax_free_amount", "0");
        parameters.put("cancel_vat_amount", "200");
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();

        if(!orderProductService.ableCancel(orders)){
            log.error("주문 취소 못하는 상태입니다.");
            throw new RuntimeException("주문 취소 못하는 상태입니다.");
        }

        ResponseEntity<KakaoCancelResponse> responseEntity = restTemplate.postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/cancel",
                requestEntity,
                KakaoCancelResponse.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            orders.cancelOrder();
            orderProductService.cancelState(orders);
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("카카오페이 취소 요청 실패: " + responseEntity.getStatusCode());
        }
    }

    @Transactional
    public KakaoCancelResponse kakaoSelectCancel(Orders orders, List<Long> orderProductIds){

        Long totalMoney = 0L;
        for(Long orderProductId: orderProductIds){
            try {
                OrderProduct orderProduct = orderProductService.findByOrderProduct(orderProductId);
                if (!orderProduct.getOrders().equals(orders)) {
                    log.error("주문 취소 불가능: OrderProduct ID {}", orderProductId);
                    continue;
                }
                orderProduct.updateStatus(OrderStatus.주문취소);
                totalMoney += (orderProduct.getProductOption().getProduct().getPrice() + orderProduct.getProductOption().getPrice()) * orderProduct.getCount();
            }catch (Exception e) {
                log.error("주문 취소 오류: OrderProduct ID {}", orderProductId, e);
            }
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", payProperties.getCid());
        parameters.put("tid", orders.getTid());
        parameters.put("cancel_amount", String.valueOf(totalMoney));
        parameters.put("cancel_tax_free_amount", "0");
        parameters.put("cancel_vat_amount", "200");
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<KakaoCancelResponse> responseEntity = restTemplate.postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/cancel",
                requestEntity,
                KakaoCancelResponse.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            orders.updateTotalMoney(orders.getTotalMoney()-totalMoney);
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("카카오페이 취소 요청 실패: " + responseEntity.getStatusCode());
        }
    }
}
