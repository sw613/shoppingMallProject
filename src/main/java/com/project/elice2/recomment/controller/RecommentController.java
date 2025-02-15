package com.project.elice2.recomment.controller;

import com.project.elice2.recomment.domain.RecommentRequestDto;
import com.project.elice2.recomment.service.RecommentService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recomment")
@RequiredArgsConstructor
public class RecommentController {

    private final RecommentService recommentService;

    @PostMapping("/create")
    public String createComment(@CurrentUser Users user, @ModelAttribute RecommentRequestDto recommentRequestDto, Model model) {

        // TODO 컨트롤러쪽에서 로그인 체크해야함
        recommentService.createRecomment(user, recommentRequestDto);

        return "redirect:/product/" + recommentRequestDto.getProductId();
    }

    @PostMapping("/update/{recommentId}")
    public String updateComment(@PathVariable Long recommentId, @ModelAttribute RecommentRequestDto recommentRequestDto) {
        recommentService.updateRecomment(recommentId, recommentRequestDto);

        return "redirect:/product/" + recommentRequestDto.getProductId();
    }

    @PostMapping("/delete/{recommentId}")
    public String deleteComment(@PathVariable Long recommentId) {
        Long productId = recommentService.deleteRecomment(recommentId);

        return "redirect:/product/" + productId;
    }
}