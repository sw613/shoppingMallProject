package com.project.elice2.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.elice2.product.domain.Product;

import com.project.elice2.category.domain.PetType;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 전체 상품
    Page<Product> findAllByAtDeleteIsNull(Pageable page);
    
    // 키워드 검색
    Page<Product> findAllByAtDeleteIsNullAndTitleContainingIgnoreCase(String keyword, Pageable page);

    // 카테고리별 상품 조회
    Page<Product> findByCategoryIdAndAtDeleteIsNull(Long categoryId, Pageable pageable);

    // PetType 별 상품 조회
    Page<Product> findByCategoryPetTypeAndAtDeleteIsNull(PetType petType, Pageable pageable);

    // 상품 id로 조회
    Optional<Product> findByIdAndAtDeleteIsNull(Long id);
}
