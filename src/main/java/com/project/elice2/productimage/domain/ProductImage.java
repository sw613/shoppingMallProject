package com.project.elice2.productimage.domain;

import com.project.elice2.global.BaseEntity;
import com.project.elice2.product.domain.Product;
import com.project.elice2.productimage.dto.ResponseProductImgDto;

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
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public ResponseProductImgDto toProductImgDto() {
    	return ResponseProductImgDto.builder()
    			.id(id)
    			.url(url)
    			.productId(product.getId())
    			.build();
    }
}
