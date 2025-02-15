package com.project.elice2.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.elice2.category.domain.Category;
import com.project.elice2.category.domain.PetType;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	List<Category> findByPetTypeAndAtDeleteIsNull(PetType petType);
	
	Optional<Category> findByIdAndAtDeleteIsNull(Long id);
}
