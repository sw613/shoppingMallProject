package com.project.elice2.comment.repository;

import com.project.elice2.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUsers_IdAndAtDeleteIsNull(Long userId);

    List<Comment> findByProduct_IdAndAtDeleteIsNull(Long productId);

    Page<Comment> findByProduct_IdAndAtDeleteIsNull(Long productId, Pageable pageable);

    Page<Comment> findAllByAtDeleteIsNull(Pageable pageable);
}
