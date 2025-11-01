package com.ptit.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping({"/", "/auth"})
    public String auth() {
        return "auth";
    }
    
    @GetMapping("/home")
    public String home() {
        return "home";
    }
    
    @GetMapping("/ranking")
    public String ranking() {
        return "ranking";
    }
    
    @GetMapping("/management")
    public String management() {
        return "management";
    }
    
    @GetMapping("/major")
    public String major() {
        return "major";
    }
}
