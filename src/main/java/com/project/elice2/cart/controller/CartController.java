package com.project.elice2.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")

public class CartController {
    @GetMapping
    public String viewCart() {
        return "cart/cart";
    }
}


