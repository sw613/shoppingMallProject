package com.project.elice2.users.controller;

import com.project.elice2.users.domain.Users;
import com.project.elice2.users.dto.SignupRequest;
import com.project.elice2.users.main.CurrentUser;
import com.project.elice2.users.repository.UserRepository;
import com.project.elice2.users.service.UserService;
import com.project.elice2.users.validation.SignupValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final SignupValidator signupValidator;


    @InitBinder("signupRequest")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(signupValidator);
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("request", new SignupRequest());
        return "users/signup/signupForm";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("request") SignupRequest request, BindingResult bindingResult, Model model, HttpSession session) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("request", request);
            return "users/signup/signupForm";
        }

        signupValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("request", request);
            return "users/signup/signupForm";
        }

        Users user = userService.signup(request);

        // 세션에 이메일 저장
        session.setAttribute("signupEmail", request.getEmail());
        return "redirect:/check-email";
    }


    @GetMapping("/check-email")
    public String checkEmail(HttpSession session, Model model, RedirectAttributes attributes) {
        String email = (String) session.getAttribute("signupEmail");
        if (email == null) {
            // 세션에 이메일 정보가 없을 경우 처리
            return "redirect:/login";
        }
        model.addAttribute("email", email);
//        session.removeAttribute("signupEmail");
        return "users/signup/checkEmail";
    }

    @GetMapping("/check-email-login")
    public String checkEmailLogin(@CurrentUser Users user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        System.out.println("#####Logged in user: " + user.getEmail());
        return "users/signup/checkEmailLogin";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model, HttpServletRequest request) {
        String view = "users/signup/checkedEmail";

        Users user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }
        if (!user.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }

        userService.completeSignup(email);
        userService.deleteToken(user);
//        userService.login(user, request);
        model.addAttribute("username", user.getUsername());
        return view;
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Users user, HttpSession session, RedirectAttributes attributes) {
        String email = (String) session.getAttribute("signupEmail");
        if (email == null && user.getEmail().isEmpty()) {
            // 세션에 이메일 정보가 없을 경우 처리
            return "redirect:/login";
        }

        if (email != null) {
            attributes.addFlashAttribute("email", email);
            attributes.addFlashAttribute("message", "이메일 인증 링크를 발송했습니다.");
            userService.sendSignupConfirmEmail(email);
            return "redirect:/check-email";
        } else {
            attributes.addFlashAttribute("email", user.getEmail());
            attributes.addFlashAttribute("message", "이메일 인증 링크를 발송했습니다.");
            userService.sendSignupConfirmEmail(user.getEmail());
            return "redirect:/check-email-login";
        }
    }

}
