package com.project.elice2.puductoption.domain;

import com.project.elice2.global.BaseEntity;
import com.project.elice2.product.domain.Product;
import com.project.elice2.puductoption.dto.ResponseOptionDto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prduct_option_id")
    private Long id;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    public ResponseOptionDto toResponseDto() {
        return ResponseOptionDto.builder()
                .id(id)
                .optionName(name)
                .optionPrice(price + product.getPrice())
                .optionCount(count)
                .productId(product.getId())
                .build();
    }

    public void updateCount(Long num){
        this.count += num;
    }
}
