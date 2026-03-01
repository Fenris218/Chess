package com.example.pbl4Version1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping(value = {"/index", "/home"})
    public String index() {
        return "user/play_with_bot";
    }
}
