package com.project.elice2.deliveryaddress.controller;

import com.project.elice2.deliveryaddress.dto.*;
import com.project.elice2.deliveryaddress.service.DeliveryAddressService;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import com.project.elice2.users.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;
    private final UserRepository userRepository;


    @GetMapping("/my-page/address")
    public String addressForm(@CurrentUser Users user, Model model) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. user id: " + user.getId());
        }

        List<AddressDto> allAddress = deliveryAddressService.findByUserId_withDto(user);

        model.addAttribute("request", new UpdateAddressRequest());
        model.addAttribute("allAddress", allAddress);
        model.addAttribute("addressCount", allAddress.size()); // 배송지 개수 추가
        return "users/settings/user/address";
    }

    @PostMapping("/my-page/address/create-or-update")
    public String createOrUpdateAddress(@CurrentUser Users user,
                                        @Valid @ModelAttribute("request") UpdateAddressRequest request,
                                        BindingResult bindingResult,
                                        Model model) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. user id: " + user.getId());
        }

        // 배송지 개수 제한 확인
        if (request.getId() == null && deliveryAddressService.findByUserId(user).size() >= 6) {
           throw new IllegalArgumentException("배송지는 최대 6개까지 등록할 수 있습니다. user id: " + user.getId());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("request", request);
            model.addAttribute("allAddress", deliveryAddressService.findByUserId_withDto(user));
            model.addAttribute("validationFailed", true); // 검증 실패 플래그 설정
            return "users/settings/user/address";
        }

        if (request.getId() != null) {
            // 수정 작업
            deliveryAddressService.updateAddress(request.getId(), request, findUser);
        } else {
            // 생성 작업
            deliveryAddressService.createAddress(request, findUser);
        }

        return "redirect:/my-page/address";
    }





    @PostMapping("/my-page/address/create")
    public String updateAddress(@CurrentUser Users user,
                                @Valid @ModelAttribute("request") UpdateAddressRequest request,
                                BindingResult bindingResult,
                                Model model) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. user id: " + user.getId());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("request", request);
            model.addAttribute("allAddress", deliveryAddressService.findByUserId_withDto(user));
            model.addAttribute("validationFailed", true); // 검증 실패 플래그 설정
            return "users/settings/user/address";
        }

        deliveryAddressService.createAddress(request, findUser);
        return "redirect:/my-page/address";
    }

    @PostMapping("/my-page/address/update/{addressId}")
    public String deleteAddress(@PathVariable Long addressId, @CurrentUser Users user,
                                @Valid @ModelAttribute("request") UpdateAddressRequest request,
                                BindingResult bindingResult,
                                Model model) {
        Users findUser = userRepository.findById(user.getId()).orElse(null);
        if (findUser == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다. user id: " + user.getId());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("request", request);
            model.addAttribute("allAddress", deliveryAddressService.findByUserId_withDto(user));
            model.addAttribute("validationFailed", true); // 검증 실패 플래그 설정
            return "users/settings/user/address";
        }

        deliveryAddressService.updateAddress(addressId, request, findUser);
        return "redirect:/my-page/address";
    }


    @PostMapping("/my-page/address/delete/{addressId}")
    public String deleteAddress(@PathVariable Long addressId, @CurrentUser Users user, Model model) {
        Users findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 없습니다. user id: " + user.getId()));

        deliveryAddressService.deleteAddress(addressId, findUser.getId());
        return "redirect:/my-page/address";
    }

}
