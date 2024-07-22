package com.nz.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nz.data.UserDTO;
import com.nz.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/join01")
    public String join01() {
        return "join01";
    }

    @GetMapping("/join02")
    public String join02() {
        return "join02";
    }

    @PostMapping("/joinPro")
    public String joinPro(UserDTO userDTO, Model model) {
        this.userService.createUser(userDTO);
        return "joinPro";
    }
    
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }
    
    @GetMapping("/mypage")
    @PreAuthorize("isAuthenticated()")
    public String mypage() {
        return "mypage";
    }
    
    @GetMapping("/myContract")
    @PreAuthorize("isAuthenticated()")
    public String myContract() {
        return "myContract";
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam("username") String username) {
        boolean exists = userService.isUsernameTaken(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}