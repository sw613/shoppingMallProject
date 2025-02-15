package com.project.elice2.orders.domain;

import com.project.elice2.global.BaseEntity;
import com.project.elice2.orderproduct.domain.OrderProduct;
import com.project.elice2.orders.dto.request.RequestOrderDto;
import com.project.elice2.orders.dto.response.ResponseOrderDto;
import com.project.elice2.orders.dto.response.ResponseOrderSuccessDto;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    private String tid;

    private String uid;

    @Column(nullable = false)
    private Long totalMoney;

    @Column(nullable = false)
    private LocalDateTime buyDate;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String request;

    @Column(nullable = false)
    private boolean state;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProductList;

    @Builder
    public Orders(Users users,String tid, RequestOrderDto requestOrderDto, PayMethod payMethod){
        this.users = users;
        this.tid = tid;
        this.totalMoney = 0L;
        this.buyDate = LocalDateTime.now();
        this.address = requestOrderDto.getAddress();
        this.phone = requestOrderDto.getPhone();
        this.recipient = requestOrderDto.getRecipient();
        this.request = requestOrderDto.getRequest();
        this.payMethod = payMethod;
        this.state = true;
    }

    public void inputUid(String uid){
        this.uid = uid;
    }


    public void updateTotalMoneyAndBuyDate(Long totalMoney){
        this.totalMoney = totalMoney;
        this.buyDate = LocalDateTime.now();
    }

    public void updateTotalMoney(Long totalMoney){
        this.totalMoney = totalMoney;
    }

    public void cancelOrder(){
        this.state = false;
    }

    public ResponseOrderSuccessDto toResponseOrderSuccessDto(){
        return new ResponseOrderSuccessDto(
                this.buyDate,
                (long) this.orderProductList.size(),
                this.totalMoney,
                this.address
        );
    }

    public ResponseOrderDto toResponseOrderDto(){
        return new ResponseOrderDto(
                this.id,
                this.totalMoney,
                this.buyDate,
                this.address,
                this.state
        );
    }

}
