package com.project.elice2.orders.controller;

import com.project.elice2.orderproduct.dto.response.ResponseOrderProductDto;
import com.project.elice2.orders.dto.response.ResponseOrderDto;
import com.project.elice2.orders.service.OrdersService;
import com.project.elice2.users.domain.Authority;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.main.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/view/payment")
@RequiredArgsConstructor
public class OrderViewsController {

    private final OrdersService ordersService;

    @GetMapping("/orders")
    public String viewOrderPage()
    {
        return "orderPage/payment";
    }
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("pg_token") String pgToken, Model model) {
        model.addAttribute("pgToken", pgToken);
        return "orderPage/paySuccess";
    }

    @GetMapping("/ordersListAdmin")
    public String viewOrderListAdminPage(@CurrentUser Users user, Model model, @RequestParam(defaultValue = "1") Long page) throws IllegalAccessException {
        Page<ResponseOrderDto> pagedOrders = ordersService.findAllOrdersAdmin(user, page - 1);
        model.addAttribute("requestList", pagedOrders.getContent());
        model.addAttribute("totalPages", pagedOrders.getTotalPages());
        model.addAttribute("page", page);
        return "orderPage/ordersListAdmin";
    }

    @GetMapping("/ordersList")
    public String viewOrderList(@CurrentUser Users user, Model model, @RequestParam(defaultValue = "1") Long page){
        Page<ResponseOrderDto> pagedOrders = ordersService.findAllOrders(user, page-1);
        model.addAttribute("requestList", pagedOrders.getContent());
        model.addAttribute("totalPages", pagedOrders.getTotalPages());
        model.addAttribute("page", page);
        return "orderPage/ordersList";
    }


    @GetMapping("/detailAdmin/{orderId}")
    public String viewOrderProductListAdmin(@CurrentUser Users user, Model model, @PathVariable Long orderId){
        List<ResponseOrderProductDto> productDtoList = ordersService.findOrdersProductAllAdmin(user, orderId);
        model.addAttribute("productDtoList", productDtoList);
        System.out.println(productDtoList);

        return "orderPage/orderDetailAdmin";
    }

    @GetMapping("/detail/{orderId}")
    public String viewOrderProductList(@CurrentUser Users user, Model model, @PathVariable Long orderId){
        List<ResponseOrderProductDto> productDtoList = ordersService.findOrdersProductAll(user, orderId);
        model.addAttribute("productDtoList", productDtoList);
        System.out.println(productDtoList);

        return "orderPage/orderDetail";
    }



}
