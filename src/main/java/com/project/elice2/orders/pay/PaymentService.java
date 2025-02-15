package com.project.elice2.orders.pay;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.project.elice2.orderproduct.domain.OrderProduct;
import com.project.elice2.orderproduct.domain.OrderStatus;
import com.project.elice2.orderproduct.service.OrderProductService;
import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.service.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrdersService ordersService;

    private final OrderProductService orderProductService;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String secretKey;

    public String getToken(String apiKey, String secretKey) throws IOException {
        URL url = new URL("https://api.iamport.kr/users/getToken");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("imp_key", apiKey);
        json.addProperty("imp_secret", secretKey);


        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Gson gson = new Gson();
        String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();
        String accessToken = gson.fromJson(response, Map.class).get("access_token").toString();
        br.close();

        conn.disconnect();

        log.info("Iamport 엑세스 토큰 발급 성공 : {}", accessToken);
        return accessToken;
    }

    public void refundRequest(String access_token, String merchant_uid, String reason) throws IOException {
        URL url = new URL("https://api.iamport.kr/payments/cancel");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", access_token);

        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("merchant_uid", merchant_uid);
        json.addProperty("reason", reason);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        br.close();
        conn.disconnect();

        log.info("결제 취소 완료 : 주문 번호 {}", merchant_uid);
    }

    public void refund(Orders orders) throws IOException {

        if(!orderProductService.ableCancel(orders)){
            log.error("주문 취소 못하는 상태입니다.");
            throw new RuntimeException("주문 취소 못하는 상태입니다.");
        }

        String access_token = getToken(apiKey, secretKey);

        URL url = new URL("https://api.iamport.kr/payments/cancel");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", access_token);

        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("merchant_uid", orders.getUid());
        json.addProperty("reason", "단순 변심");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        br.close();
        conn.disconnect();

        orders.cancelOrder();
        orderProductService.cancelState(orders);

        log.info("결제 취소 완료 : 주문 번호 {}", orders.getUid());
    }

    public void selectRefund(Orders orders, List<Long> orderProductIds) throws IOException {
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

        String access_token = getToken(apiKey, secretKey);

        URL url = new URL("https://api.iamport.kr/payments/cancel");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");

        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", access_token);

        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("merchant_uid", orders.getUid());
        json.addProperty("amount", totalMoney);
        json.addProperty("reason", "단순 변심");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        br.close();
        conn.disconnect();

        orders.updateTotalMoney(orders.getTotalMoney()-totalMoney);
    }
}
