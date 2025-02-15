package com.project.elice2.recomment.service;

import com.project.elice2.comment.domain.Comment;
import com.project.elice2.comment.repository.CommentRepository;
import com.project.elice2.product.domain.Product;
import com.project.elice2.product.repository.ProductRepository;
import com.project.elice2.product.service.ProductService;
import com.project.elice2.recomment.domain.Recomment;
import com.project.elice2.recomment.domain.RecommentRequestDto;
import com.project.elice2.recomment.domain.RecommentResponseDto;
import com.project.elice2.recomment.repository.RecommentRepository;
import com.project.elice2.users.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RecommentService {

    private final RecommentRepository recommentRepository;
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;

    @Transactional
    public RecommentResponseDto createRecomment(Users users, RecommentRequestDto recommentRequestDto) {

        Comment findComment = commentRepository.findById(recommentRequestDto.getCommentId()).orElseThrow(() -> new NoSuchElementException("Not Found recomment id: " + recommentRequestDto.getCommentId()));

        // productId로 product 찾기
        Product product = productRepository.findByIdAndAtDeleteIsNull(recommentRequestDto.getProductId()).orElseThrow(() -> new NoSuchElementException("Not Found product id: " + recommentRequestDto.getProductId()));

        // Recomment 생성
        Recomment recomment = Recomment.builder()
                .detail(recommentRequestDto.getDetail())
                .comment(findComment)
                .users(users)
                .product(product)
                .build();

        // recomment 저장
        recommentRepository.save(recomment);

        return recomment.recommentToRecommentResponseDto();
    }

    @Transactional(readOnly = true)
    public void findById(Long recommentId) {

    }

    @Transactional
    public RecommentResponseDto updateRecomment(Long recommentId, RecommentRequestDto recommentRequestDto) {

        // Recommnet 찾기
        Recomment findRecomment = recommentRepository.findById(recommentId).orElseThrow(() -> new NoSuchElementException("Not Found recomment id " + recommentId));

        // Recomment 업데이트
        findRecomment.updateRecommnt(recommentRequestDto);

        recommentRepository.save(findRecomment);

        return findRecomment.recommentToRecommentResponseDto();
    }

    @Transactional
    public Long deleteRecomment(Long recommentId) {

        // Recommnet 찾기
        Recomment findRecomment = recommentRepository.findById(recommentId).orElseThrow(() -> new NoSuchElementException("Not Found recomment id " + recommentId));

        // soft delete
        findRecomment.setAtDelete();

        recommentRepository.save(findRecomment);

        return findRecomment.getProduct().getId();
    }
}
