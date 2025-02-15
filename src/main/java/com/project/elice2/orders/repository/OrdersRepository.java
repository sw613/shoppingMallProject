package com.project.elice2.orders.repository;

import com.project.elice2.orders.domain.Orders;
import com.project.elice2.users.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Page<Orders> findAllByUsers(Users users, Pageable pageable);

    List<Orders> findAllByUsers(Users users);
}
