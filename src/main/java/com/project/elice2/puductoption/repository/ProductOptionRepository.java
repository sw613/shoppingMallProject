package com.project.elice2.puductoption.repository;

import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.elice2.product.domain.Product;
import com.project.elice2.puductoption.domain.ProductOption;


@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long>{
	List<ProductOption> findByProduct(Product product);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT po FROM ProductOption po WHERE po.id = :optionId")
	ProductOption findWithLock(@Param("optionId") Long optionId);
}
