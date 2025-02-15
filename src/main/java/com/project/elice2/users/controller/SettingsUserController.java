package com.project.elice2.users.controller;

import com.project.elice2.deliveryaddress.service.DeliveryAddressService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.dto.DeleteAccountRequest;
import com.project.elice2.deliveryaddress.dto.UpdateAddressRequest;
import com.project.elice2.users.dto.UpdatePasswordRequest;
import com.project.elice2.users.dto.UpdateUserInfoRequest;
import com.project.elice2.users.main.CurrentUser;
import com.project.elice2.users.repository.UserRepository;
import com.project.elice2.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsUserController {

    private final UserService userService;
    private final UserRepository userRepository;


    @GetMapping("/my-page")
    public String updateUserInfoForm(@CurrentUser Users user, Model model) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        model.addAttribute("request", new UpdateUserInfoRequest(findUser));
        return "users/settings/user/myPage";
    }

    @PostMapping("/my-page/update-username")
    public String updateUsername(@Valid @ModelAttribute("request") UpdateUserInfoRequest request,
                                 BindingResult bindingResult,
                                 @CurrentUser Users user,
                                 Model model,
                                 RedirectAttributes attributes){
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("request", request);
            return "users/settings/user/myPage";
        }

        userService.updateUsername(findUser, request.getUsername());
        attributes.addFlashAttribute("request", new UpdateUserInfoRequest(findUser));
        attributes.addFlashAttribute("message", "이름을 변경했습니다.");
        return "redirect:/my-page";
    }

    @PostMapping("/my-page/update-phone-number")
    public String updatePhoneNumber(@Valid @ModelAttribute("request") UpdateUserInfoRequest request,
                                 BindingResult bindingResult,
                                 @CurrentUser Users user,
                                 Model model,
                                 RedirectAttributes attributes){
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("request", request);
            return "users/settings/user/myPage";
        }

        userService.updatePhoneNumber(findUser, request.getPhoneNumber());
        attributes.addFlashAttribute("request", new UpdateUserInfoRequest(findUser));
        attributes.addFlashAttribute("message", "전화번호를 변경했습니다.");
        return "redirect:/my-page";
    }



    @GetMapping("/my-page/password")
    public String updatePasswordForm(Model model, @CurrentUser Users user) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        model.addAttribute("user", findUser);
        model.addAttribute("request", new UpdatePasswordRequest());
        return "users/settings/user/password";
    }

    @PostMapping("/my-page/update-password")
    public String updatePassword(@Valid @ModelAttribute("request") UpdatePasswordRequest request,
                                 BindingResult bindingResult,
                                 @CurrentUser Users user,
                                 Model model,
                                 RedirectAttributes attributes) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", findUser);
            return "users/settings/user/password";
        }

        userService.updatePassword(findUser, request.getPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:/my-page/password";
    }

    @GetMapping("/my-page/withdrawal")
    public String deleteAccountForm(Model model, @CurrentUser Users user) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        model.addAttribute("request", new DeleteAccountRequest());
        return "users/settings/user/withdrawal";
    }

    @PostMapping("/my-page/withdrawal")
    public String deleteAccount(@Valid @ModelAttribute("request") DeleteAccountRequest request,
                                 BindingResult bindingResult,
                                 @CurrentUser Users user,
                                 Model model,
                                 RedirectAttributes attributes) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }
        if (!findUser.equals(user)) {
            return "users/settings/accessDenied";
        }

        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            // 유효성 검증 오류를 모델에 추가
            model.addAttribute("user", findUser);
            model.addAttribute("request", request);
            return "users/settings/user/withdrawal"; // 오류 메시지와 함께 페이지 재렌더링
        }

        userService.deleteUser(findUser);
        return "redirect:/logout"; //TODO 수정해야 함
    }
}
