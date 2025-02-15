package com.project.elice2.orderproduct.repository;

import com.project.elice2.orderproduct.domain.OrderProduct;
import com.project.elice2.orders.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findAllByOrders(Orders orders);
}
