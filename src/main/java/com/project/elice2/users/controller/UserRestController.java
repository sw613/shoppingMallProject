package com.project.elice2.users.controller;

import com.project.elice2.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;


    @PostMapping("/validate-email")
    public @ResponseBody String emailCheck(@RequestParam("email") String email) {
        System.out.println("검증할 이메일: " + email);
        String result = userService.validateEmail(email);
        System.out.println("검증 결과: " + result);
        return result;
    }
}
