package com.project.elice2.likeprduct.domain;

import com.project.elice2.product.domain.Product;
import com.project.elice2.users.domain.Users;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LikeProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

}
