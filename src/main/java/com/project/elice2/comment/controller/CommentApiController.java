package com.project.elice2.comment.controller;

import com.project.elice2.comment.domain.CommentRequestDto;
import com.project.elice2.comment.domain.CommentResponseDto;
import com.project.elice2.comment.service.CommentService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/create/{productId}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long productId, @RequestBody CommentRequestDto commentRequestDto, @CurrentUser Users users) {

        CommentResponseDto commentResponseDto = commentService.createComment(users, commentRequestDto);

        return ResponseEntity.ok(commentResponseDto);
    }

    @GetMapping("/findusers/{userId}")
    public ResponseEntity<List<CommentResponseDto>> findByUserId(@PathVariable Long userId) {

        List<CommentResponseDto> commentResponseDtos = commentService.findByUsersId(userId);

        return ResponseEntity.ok(commentResponseDtos);
    }

    @GetMapping("/findproduct/{productId}")
    public ResponseEntity<List<CommentResponseDto>> findByProductId(@PathVariable Long productId) {

        List<CommentResponseDto> commentResponseDtos = commentService.findByProductId(productId);

        return ResponseEntity.ok(commentResponseDtos);
    }

    @PatchMapping("/update/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long commentId, @ModelAttribute CommentRequestDto commentRequestDto) {

        CommentResponseDto commentResponseDto = commentService.updateComment(commentId, commentRequestDto);

        return ResponseEntity.ok(commentResponseDto);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);

        return ResponseEntity.ok("delete success");
    }
}