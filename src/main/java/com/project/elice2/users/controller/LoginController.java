package com.project.elice2.users.controller;

import com.project.elice2.users.domain.Users;
import com.project.elice2.users.dto.EmailLoginDto;
import com.project.elice2.users.repository.UserRepository;
import com.project.elice2.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final UserService userService;


    @GetMapping("/login")
    public String login() {
        return "users/login/login";
    }

    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "users/login/emailLogin";
    }

    @PostMapping("/email-login")
    public String emailLoginLink(String email, Model model, RedirectAttributes attributes) {
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            model.addAttribute("error", "유효한 이메일 주소가 아닙니다.");
            return "users/login/emailLogin";
        }

        userService.sendLoginLink(user);
        attributes.addFlashAttribute("message", "로그인 링크를 이메일로 발송했습니다.");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model, HttpServletRequest request) {
        String view = "users/login/loggedInByEmail";

        Users user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !user.isValidToken(token)) {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }

        userService.login(user, request);
        userService.deleteToken(user);
        return view;
    }
}
