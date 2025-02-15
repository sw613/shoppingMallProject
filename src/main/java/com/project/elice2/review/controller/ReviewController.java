package com.project.elice2.review.controller;

import com.project.elice2.review.domain.ReviewRequestDto;
import com.project.elice2.review.service.ReviewService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create")
    public String createReview(@CurrentUser Users users,
                               @ModelAttribute ReviewRequestDto reviewRequestDto,
                               BindingResult bindingResult) {

        reviewService.createReview(users, reviewRequestDto);

        return "redirect:/product/" + reviewRequestDto.getProductId();
    }

    @PostMapping("/update/{reviewId}")
    public String updateReview(@PathVariable Long reviewId, @ModelAttribute ReviewRequestDto reviewRequestDto) {

        reviewService.updateReview(reviewId, reviewRequestDto);

        return "redirect:/product/" + reviewRequestDto.getProductId();
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        Long productId = reviewService.deleteReview(reviewId);

        return "redirect:/product/" + productId;
    }

}
