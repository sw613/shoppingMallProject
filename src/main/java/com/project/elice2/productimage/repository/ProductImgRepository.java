package com.project.elice2.productimage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.elice2.product.domain.Product;
import com.project.elice2.productimage.domain.ProductImage;


@Repository
public interface ProductImgRepository extends JpaRepository<ProductImage, Long>{
	List<ProductImage> findByProduct(Product product);
	
	Optional<ProductImage> findByUrl(String url);
}
