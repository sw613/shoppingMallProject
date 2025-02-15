package com.project.elice2.users.main;

import com.project.elice2.users.domain.Users;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class MainController {

    @GetMapping("/main")
    public String home(@CurrentUser Users user, Model model) {
        if (user != null) {
            model.addAttribute("user", user);
            System.out.println("#####Logged in user: " + user.getEmail());
        }
        return "users/index";
    }

}
