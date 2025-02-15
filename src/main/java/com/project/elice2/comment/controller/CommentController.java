package com.project.elice2.comment.controller;

import com.project.elice2.comment.domain.CommentRequestDto;
import com.project.elice2.comment.domain.CommentResponseDto;
import com.project.elice2.comment.service.CommentService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public String createComment(@CurrentUser Users user, @ModelAttribute CommentRequestDto commentRequestDto, BindingResult bindingResult, Model model) {

        commentService.createComment(user, commentRequestDto);

        return "redirect:/product/" + commentRequestDto.getProductId();
    }

    @GetMapping("/admin")
    public String findAllComment(@CurrentUser Users users,
                                 @RequestParam(defaultValue = "1") int commentPage, @RequestParam(defaultValue = "5") int commentSize,
                                 Model model) {

        Page<CommentResponseDto> commentResponseDtoPage = commentService.findAll(PageRequest.of(commentPage - 1, commentSize, Sort.by("atCreate").descending()));
        model.addAttribute("comments", commentResponseDtoPage.getContent());
        model.addAttribute("commentTotalPages", commentResponseDtoPage.getTotalPages());

        model.addAttribute("user", users);

        return "users/settings/admin/recommnetPage";
    }

    @PostMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId) {
        Long productId = commentService.deleteComment(commentId);

        return "redirect:/product/" + productId;
    }

    @PostMapping("/update/{commentId}")
    public String updateComment(@PathVariable Long commentId, @ModelAttribute CommentRequestDto commentRequestDto) {
        commentService.updateComment(commentId, commentRequestDto);

        return "redirect:/product/" + commentRequestDto.getProductId();
    }
}