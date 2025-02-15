package com.project.elice2.users.controller;

import com.project.elice2.users.domain.Users;
import com.project.elice2.users.dto.*;
import com.project.elice2.users.main.CurrentUser;
import com.project.elice2.users.repository.UserRepository;
import com.project.elice2.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin-page")
public class SettingsAdminController {

    private final UserService userService;
    private final UserRepository userRepository;


    @GetMapping("/users")
    public String userListForm(@CurrentUser Users user,
                               UserSearchCondition condition,
                               @PageableDefault(size = 10) Pageable pageable,
                               Model model) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        Page<UserDto> findUsers = userService.findUsers(condition, pageable);
        model.addAttribute("users", findUsers);
        model.addAttribute("condition", condition);
        return  "users/settings/admin/userList";
    }


    @GetMapping("/my-info")
    public String updateAdminInfoForm(@CurrentUser Users user, Model model) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        model.addAttribute("request", new UpdateUserInfoRequest(findUser));
        return "users/settings/admin/adminPage";
    }

    @PostMapping("/update-username")
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
            return "users/settings/admin/adminPage";
        }

        userService.updateUsername(findUser, request.getUsername());
        attributes.addFlashAttribute("request", new UpdateUserInfoRequest(findUser));
        attributes.addFlashAttribute("message", "이름을 변경했습니다.");
        return "redirect:/admin-page/my-info";
    }

    @PostMapping("/update-phone-number")
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
            return "users/settings/admin/adminPage";
        }

        userService.updatePhoneNumber(findUser, request.getPhoneNumber());
        attributes.addFlashAttribute("request", new UpdateUserInfoRequest(findUser));
        attributes.addFlashAttribute("message", "전화번호를 변경했습니다.");
        return "redirect:/admin-page/my-info";
    }

    @GetMapping("/password")
    public String updatePasswordForm(@CurrentUser Users user, Model model) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        model.addAttribute("user", findUser);
        model.addAttribute("request", new UpdatePasswordRequest());
        return "users/settings/admin/password";
    }

    @PostMapping("/update-password")
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
            return "users/settings/admin/password";
        }

        userService.updatePassword(findUser, request.getPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:/admin-page/password";
    }

    @GetMapping("/withdrawal")
    public String deleteAccountForm(Model model, @CurrentUser Users user) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. userid: " + user.getId());
        }

        return "users/settings/admin/withdrawal";
    }

    @PostMapping("/withdrawal")
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

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", findUser);
            return "users/settings/admin/withdrawal";
        }

        userService.deleteUser(findUser);
        return "redirect:/test/main";
    }



}
