/*
package com.project.elice2.orders.service;

import com.project.elice2.orderproduct.service.OrderProductService;
import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.dto.request.RequestOrderDto;
import com.project.elice2.orders.dto.response.ResponseResultOptionDto;
import com.project.elice2.orders.repository.OrdersRepository;
import com.project.elice2.product.domain.Product;
import com.project.elice2.puductoption.domain.ProductOption;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;


class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderProductService orderProductService;

    @InjectMocks
    private OrdersService ordersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByOrdersId() {
    }

    @Test
    void cteateOrder() {

        // Given
        //user 생성
        Users mockUser = new Users();
        mockUser.setId(1L);

        // 주문 리스트 dto
        List<RequestOrderDto> requestOrderDtoList = List.of(
                new RequestOrderDto(1L, 2L),
                new RequestOrderDto(2L, 3L),
                new RequestOrderDto(3L, 2L)
        );



        //상품 옵션 생성
        ProductOption productOption1 = new ProductOption();
        productOption1.setId(1L);
        productOption1.setPrice(20L);
        productOption1.setCount(5L);

        ProductOption productOption2 = new ProductOption();
        productOption2.setId(2L);
        productOption2.setPrice(15L);
        productOption2.setCount(10L);

        //mock orders 데이터 생성
        Orders mockOrder = Orders.builder()
                .users(mockUser)
                .build();


        //반환 받는 값
        ResponseResultOptionDto mockResultOption = new ResponseResultOptionDto(1000L, true);

        // Mock 설정
        when(ordersRepository.save(mockOrder)).thenReturn(mockOrder);
        when(orderProductService.craeteOrderProducts(mockOrder,  requestOrderDtoList))
                .thenReturn(mockResultOption);

        //orders 생성
        Boolean result = ordersService.cteateOrder(requestOrderDtoList);

        assertThat(result).isTrue();


    }

    @Test
    void findAllOrders() {
    }

    @Test
    void findOrdersProductAll() {
    }

    @Test
    void deleteOrderProduct() {
    }
}*/
