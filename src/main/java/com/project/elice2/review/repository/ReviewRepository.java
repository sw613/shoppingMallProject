package com.project.elice2.review.repository;

import com.project.elice2.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUsers_Id(Long usersId);

    List<Review> findByProduct_Id(Long productId);

    List<Review> findByUsers_IdAndAtDeleteIsNull(Long userId);

    List<Review> findByProduct_IdAndAtDeleteIsNull(Long productId);

    Page<Review> findByProduct_IdAndAtDeleteIsNull(Long productId, Pageable pageable);


}
