package com.project.elice2.orderproduct.domain;

import com.project.elice2.global.BaseEntity;
import com.project.elice2.orderproduct.dto.response.ResponseOrderProductDto;
import com.project.elice2.orders.domain.Orders;
import com.project.elice2.puductoption.domain.ProductOption;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @Column(nullable = false)
    private Long count;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;

    @Builder
    public OrderProduct(Long count, Orders orders, ProductOption productOption){
        this.count = count;
        this.orderStatus = OrderStatus.주문완료;
        this.orders = orders;
        this.productOption = productOption;
    }

    public ResponseOrderProductDto toResponseOrderProductDto(){
        return new ResponseOrderProductDto(
            this.id,
            this.productOption.getId(),
            this.productOption.getProduct().getTitle()+" "+this.productOption.getName(),
            this.productOption.getPrice() + this.productOption.getProduct().getPrice(),
            this.count,
            this.productOption.getProduct().getReqImage(),
            this.orderStatus
        );
    }

    public void updateStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }
}
