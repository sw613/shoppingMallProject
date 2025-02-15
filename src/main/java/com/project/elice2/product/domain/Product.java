package com.project.elice2.product.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.project.elice2.category.domain.Category;
import com.project.elice2.global.BaseEntity;
import com.project.elice2.product.dto.ResponseProductDto;
import com.project.elice2.product.dto.ResponseProductListDto;
import com.project.elice2.puductoption.domain.ProductOption;
import com.project.elice2.users.domain.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
public class Product extends BaseEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String detail;

    private String reqImage;
    private LocalDateTime atDelete;
    
    @ColumnDefault("0")
    private Long viewCount;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    
    @PrePersist
    public void prePersist() {
        if (viewCount == null) {
            viewCount = 0L;
        }
    }   
    
    public ResponseProductDto toResponseDto() {
	    	
    	return ResponseProductDto.builder()
	    			.id(id)
	    			.title(title)
	    			.detail(detail)
	    			.reqImage(reqImage)
	    			.viewCount(viewCount)
	    			.price(price)
	    			.count(count)
	    			.categoryId(category.getId())
	    			.categoryName(category.getName())
	    			.petType(category.getPetType().name())
	    			.build();
	 }
    
    
    public ResponseProductListDto toResponseListDto(ProductOption option) {

        return ResponseProductListDto.builder()
                .optionId(option.getId())
                .title(this.title + " " + option.getName())
                .price(option.getPrice())
                .count(option.getCount())
                .reqImage(reqImage)
                .build();
    }
}
