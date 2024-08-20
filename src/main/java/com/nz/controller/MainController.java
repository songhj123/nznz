package com.nz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nz.data.UserDTO;
import com.nz.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	@Autowired
	private final UserService userService;
    
    @GetMapping("/home")
    public String home() {
        return "home/home";
    }
    
    @GetMapping("/map")
    public String map() {
        return "layout/map";
    }

    @GetMapping("/admin")
    public String admin() {
    	return "admin/adminHome";
    }
    
    @GetMapping("/admin/userList")
    public String adminUserList(Model model,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size,
                                @RequestParam(value = "role", required = false) String role) {
        Page<UserDTO> userList;

        if (role == null || role.isEmpty()) {
            userList = userService.getAllMemberPaged(PageRequest.of(page, size));
        } else {
            userList = userService.getUsersByRolePaged(role, PageRequest.of(page, size));
        }

        model.addAttribute("userList", userList);
        model.addAttribute("currentRole", role);
        return "admin/userList";
    }
    
    @PostMapping("/admin/user/updateStatus")
    public String updateUserRoles(@RequestParam("selectedUsers") List<String> usernames,
                                  @RequestParam("action") String role) {
        userService.updateUserRoles(usernames, role);
        return "redirect:/admin/userList";
    }
}