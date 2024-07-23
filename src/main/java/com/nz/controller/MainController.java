package com.nz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    @GetMapping("/home")
    public String home() {
        return "home/home"; // 변경된 경로로 수정
    }
    
    @GetMapping("/map")
    public String map() {
        return "layout/map"; // 적절한 경로로 수정
    }

}
