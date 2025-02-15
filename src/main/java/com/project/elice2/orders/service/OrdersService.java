package com.project.elice2.orders.service;

import com.project.elice2.orderproduct.domain.OrderProduct;
import com.project.elice2.orderproduct.dto.response.ResponseOrderProductDto;
import com.project.elice2.orderproduct.service.OrderProductService;
import com.project.elice2.orders.domain.Orders;
import com.project.elice2.orders.dto.request.RequestOrderDto;
import com.project.elice2.orders.dto.response.ResponseOrderDto;
import com.project.elice2.orders.dto.response.ResponseOrderSuccessDto;
import com.project.elice2.orders.dto.response.ResponseResultOptionDto;
import com.project.elice2.orders.repository.OrdersRepository;
import com.project.elice2.users.domain.Authority;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrderProductService orderProductService;

    public Orders findByOrdersId(Long ordersId){
        return ordersRepository.findById(ordersId)
                .orElseThrow(()-> new EntityNotFoundException("해당 OrderId 데이터 없음 :" + ordersId));
    }

    @Transactional
    public Long cteateOrder(Users users, RequestOrderDto requestOrderDto,String tid, String uid){
        Orders order = requestOrderDto.toEntity(users, tid);
        if(uid != null){
            order.inputUid(uid);
        }
        System.out.println(requestOrderDto);
        ordersRepository.save(order);

        ResponseResultOptionDto resultOption = orderProductService.craeteOrderProducts(order, requestOrderDto.getOrderProductDtoList());

        if(!resultOption.isState())
        {
            throw new IllegalStateException("상품 주문 중 오류");
        }
        order.updateTotalMoneyAndBuyDate(resultOption.getTotalPrice());
        return order.getId();
    }

    public Page<ResponseOrderDto> findAllOrders(Users users, Long page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "buyDate");
        Pageable pageable = PageRequest.of(Math.toIntExact(page), 15, sort);
        return ordersRepository.findAllByUsers(users, pageable)
                .map(Orders::toResponseOrderDto);
    }

    public List<ResponseOrderProductDto> findOrdersProductAllAdmin(Users user, Long ordersId) {
        if(user.getAuthority().equals(Authority.ROLE_ADMIN)) {
            Orders orders = findByOrdersId(ordersId);
            return orderProductService.findProductOptionByOrder(orders);
        }
        else{
            throw new RuntimeException("권한이 없습니다. 해당 주문을 조회할 수 없습니다.");
        }
    }

    public List<ResponseOrderProductDto> findOrdersProductAll(Users user, Long ordersId){
        Orders orders = findByOrdersId(ordersId);
        if(!orders.getUsers().equals(user))
            throw new RuntimeException("자신의 주문 내역이 아닙니다.");
        return orderProductService.findProductOptionByOrder(orders);
    }
    //사용 불확실
    /*
    @Transactional
    public boolean deleteOrder(Long orderId){
        Orders orders = findByOrdersId(orderId);
        ordersRepository.delete(orders);
        return true;
    }
     */

    public ResponseOrderSuccessDto orderSuccess(Users user, Long orderId){
        Orders orders = findByOrdersId(orderId);
        return orders.toResponseOrderSuccessDto();
    }


    @Transactional
    public boolean deleteOrderProduct(Long ordersId, Long orderProductId){
        Orders orders = findByOrdersId(ordersId);
        OrderProduct orderProduct = orderProductService.findByOrderProduct(orderProductId);

        if(!orders.equals(orderProduct.getOrders())){
            log.error("주문과 주문목록 불일치");
            return false;
        }

        Long minusMoney = orderProduct.getProductOption().getPrice() * orderProduct.getCount();

        boolean b = orderProductService.deleteOrderProduct(orderProduct);
        if(!b){
            log.error("주문 삭제 에러");
            return false;
        }
        orders.updateTotalMoney(orders.getTotalMoney() - minusMoney);
        return true;
    }

    public Page<ResponseOrderDto> findAllOrdersAdmin(Users user, Long page) throws IllegalAccessException {
        System.out.println(user.getAuthority());
        if(user.getAuthority().equals(Authority.ROLE_ADMIN)) {
            Sort sort = Sort.by(Sort.Direction.DESC, "buyDate");
            Pageable pageable = PageRequest.of(Math.toIntExact(page), 15, sort);
            return ordersRepository.findAll(pageable).map(Orders::toResponseOrderDto);
        }else{
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
    }

    public Boolean checkReveiw(Users users, Long productId){
        List<Orders> ordersList = ordersRepository.findAllByUsers(users);
        return ordersList.stream()
                .flatMap(order -> order.getOrderProductList().stream())
                .anyMatch(orderProduct ->
                        orderProduct.getProductOption().getProduct().getId().equals(productId));
    }
}
