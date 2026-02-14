package com.svr.ecommerce.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/")
    String index(Model model) {
        model.addAttribute("username", "Venkata");
        return "index";
    }
}
