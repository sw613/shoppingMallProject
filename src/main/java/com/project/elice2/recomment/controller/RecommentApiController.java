package com.project.elice2.recomment.controller;

import com.project.elice2.recomment.domain.RecommentRequestDto;
import com.project.elice2.recomment.domain.RecommentResponseDto;
import com.project.elice2.recomment.service.RecommentService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recomment")
@RequiredArgsConstructor
public class RecommentApiController {

    private final RecommentService recommentService;

    @PostMapping("/create/{userId}/{productId}/{commentId}")
    public ResponseEntity<RecommentResponseDto> createRecommnet(@CurrentUser Users users,
                                                                @ModelAttribute RecommentRequestDto recommentRequestDto) {

        RecommentResponseDto recommentResponseDto = recommentService.createRecomment(users, recommentRequestDto);

        return ResponseEntity.ok(recommentResponseDto);
    }

    @PatchMapping("/update/{recommentId}")
    public ResponseEntity<RecommentResponseDto> updateRecomment(@PathVariable Long recommentId, @ModelAttribute RecommentRequestDto recommentRequestDto) {

        RecommentResponseDto recommentResponseDto = recommentService.updateRecomment(recommentId, recommentRequestDto);

        return ResponseEntity.ok(recommentResponseDto);
    }

    @DeleteMapping("/delete/{recommentId}")
    public ResponseEntity<String> deleteRecomment(@PathVariable Long recommentId) {
        recommentService.deleteRecomment(recommentId);

        return ResponseEntity.ok("delete");
    }
}