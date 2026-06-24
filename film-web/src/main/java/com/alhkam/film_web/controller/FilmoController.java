package com.alhkam.film_web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/filmo")
public class FilmoController {

    @GetMapping
    public String showHomePage() {
        return "filmo/index";
    }
}
