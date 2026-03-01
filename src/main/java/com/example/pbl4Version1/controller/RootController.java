package com.example.pbl4Version1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectToBot() {
        return "redirect:/home";
    }
}
