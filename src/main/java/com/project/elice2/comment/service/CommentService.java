package com.project.elice2.comment.service;

import com.project.elice2.comment.domain.Comment;
import com.project.elice2.comment.domain.CommentRequestDto;
import com.project.elice2.comment.domain.CommentResponseDto;
import com.project.elice2.comment.repository.CommentRepository;
import com.project.elice2.product.domain.Product;
import com.project.elice2.product.repository.ProductRepository;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto createComment(Users users, CommentRequestDto commentRequestDto) {

        // productId로 product 찾기
        Product product = productRepository.findByIdAndAtDeleteIsNull(commentRequestDto.getProductId()).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + commentRequestDto.getProductId()));

        // Comment 생성
        Comment comment = Comment.builder()
                .detail(commentRequestDto.getDetail())
                .isSecret(commentRequestDto.getIsSecret())
                .users(users)
                .product(product)
                .build();

        // Comment 저장
        commentRepository.save(comment);

        return comment.commentToCommentResponseDto();
    }


    @Transactional(readOnly = true)
    public Page<CommentResponseDto> findAll(Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findAllByAtDeleteIsNull(pageable);

        return commentPage.map(comment -> comment.commentToCommentResponseDto());
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> findByUsersId(Long userId) {
        List<Comment> comments = commentRepository.findByUsers_IdAndAtDeleteIsNull(userId);

        return comments.stream()
                .map(comment -> comment.commentToCommentResponseDto())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> findByProductId(Long productId) {
        List<Comment> comments = commentRepository.findByProduct_IdAndAtDeleteIsNull(productId);

        return comments.stream()
                .map(comment -> comment.commentToCommentResponseDto())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> findByProductId(Long productId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByProduct_IdAndAtDeleteIsNull(productId, pageable);

        return commentPage.map(comment -> comment.commentToCommentResponseDto());
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto) {

        // Commnet 찾기
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("Not Found comment id: " + commentId));

        // Comment update
        findComment.updateComment(commentRequestDto);

        // Comment 수정
        commentRepository.save(findComment);

        return findComment.commentToCommentResponseDto();

    }

    @Transactional
    public Long deleteComment(Long commentId) {
        // Commnet 찾기
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException("Not Found comment id: " + commentId));

        // Soft delete, Recomment 같이 삭제
        findComment.setAtDelete();

        commentRepository.save(findComment);

        return findComment.getProduct().getId();
    }
}