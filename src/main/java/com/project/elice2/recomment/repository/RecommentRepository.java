package com.project.elice2.recomment.repository;

import com.project.elice2.recomment.domain.Recomment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommentRepository extends JpaRepository<Recomment, Long> {
}
