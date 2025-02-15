package com.project.elice2.orderproduct.service;

import com.project.elice2.orderproduct.domain.OrderProduct;
import com.project.elice2.orderproduct.domain.OrderStatus;
import com.project.elice2.orderproduct.dto.request.RequestDeliveryDto;
import com.project.elice2.orderproduct.dto.response.ResponseOrderProductDto;
import com.project.elice2.orderproduct.repository.OrderProductRepository;
import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.dto.request.RequestOrderDto;
import com.project.elice2.orders.dto.request.RequestOrderProductDto;
import com.project.elice2.orders.dto.response.ResponseResultOptionDto;
import com.project.elice2.puductoption.domain.ProductOption;
import com.project.elice2.puductoption.service.ProductOptionService;
import com.project.elice2.users.domain.Authority;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;
    private final ProductOptionService productOptionService;

    public OrderProduct findByOrderProduct(Long orderProductId){
        return orderProductRepository.findById(orderProductId)
                .orElseThrow(()-> new EntityNotFoundException("orderProduct 찾지 못함 " + orderProductId));
    }

    @Transactional
    public ResponseResultOptionDto craeteOrderProducts(Orders orders, List<RequestOrderProductDto> requestProducts){
        Long totalPrice = 0L;
        List<OrderProduct> orderProductList = new ArrayList<>();
        for(RequestOrderProductDto requestOrderDto : requestProducts){
            ProductOption productOption = productOptionService.findProductOption(requestOrderDto.getOptionId());
            if(productOption.getCount() < requestOrderDto.getCount()){
                log.error("{}의 재고 수량 부족항",productOption.getName());
                return new ResponseResultOptionDto(totalPrice, false);
            }
            productOption.updateCount(requestOrderDto.getCount()*-1);
            totalPrice += (productOption.getPrice() + productOption.getProduct().getPrice())*requestOrderDto.getCount();
            System.out.println("상품 가격 " + productOption.getPrice() + " 수량 " + requestOrderDto.getCount());
            orderProductList.add(new OrderProduct(requestOrderDto.getCount(), orders, productOption));
        }

        if(totalPrice < 30000)
            totalPrice+=3000;

        orderProductRepository.saveAll(orderProductList);

        return new ResponseResultOptionDto(totalPrice, true);
    }

    public List<ResponseOrderProductDto> findProductOptionByOrder(Orders orders){
        return orderProductRepository.findAllByOrders(orders).stream()
                .map(OrderProduct :: toResponseOrderProductDto)
                .toList();
    }

    @Transactional
    public Boolean updateDeliveryUpdate(Users user, RequestDeliveryDto requestDeliveryDto){
        if(!user.getAuthority().equals(Authority.ROLE_ADMIN))
            throw new RuntimeException("접근권한 없음");
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(requestDeliveryDto.getIsState());
            OrderProduct orderProduct = findByOrderProduct(requestDeliveryDto.getOrderProductId());
            if(orderProduct.getOrderStatus().equals(OrderStatus.주문취소))
                return false;
            orderProduct.updateStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Transactional
    public Boolean cancelState(Orders orders) {
        try {
            List<OrderProduct> orderProducts = orderProductRepository.findAllByOrders(orders);

            if (orderProducts == null || orderProducts.isEmpty()) {
                return false;
            }

            orderProducts.forEach(orderProduct -> {
                orderProduct.updateStatus(OrderStatus.주문취소);
//                orderProduct.getProductOption().updateCount(orderProduct.getCount());
                System.out.println("OrderProduct ID: " + orderProduct.getId() + " 상태가 '주문취소'로 변경됨");
            });

        } catch (IllegalArgumentException e) {
            System.err.println("CancelState 예외 발생: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Boolean ableCancel(Orders orders){
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrders(orders);
        if (orderProducts == null || orderProducts.isEmpty()) {
            return false;
        }
        orderProducts.forEach(orderProduct -> {
            System.out.println("OrderProduct ID: " + orderProduct.getId());
            System.out.println("Status: " + orderProduct.getOrderStatus());
        });

        boolean allCompleted = orderProducts.stream()
                .allMatch(orderProduct -> orderProduct.getOrderStatus() == OrderStatus.주문완료);

        System.out.println(allCompleted);
        if (allCompleted) {
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteOrderProduct(OrderProduct orderProduct){

        if(!orderProduct.getOrderStatus().equals(OrderStatus.주문완료))
            return false;

        orderProductRepository.delete(orderProduct);
        return true;
    }

    @Transactional
    public boolean cancelSelectState(List<Long> orderProductIds){
        for(Long orderProductId: orderProductIds){
            OrderProduct orderProduct = findByOrderProduct(orderProductId);
            orderProduct.updateStatus(OrderStatus.주문취소);
        }
        return true;
    }
}
