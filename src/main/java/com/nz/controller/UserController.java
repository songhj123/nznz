package com.nz.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nz.data.ContractDTO;
import com.nz.data.UserDTO;
import com.nz.service.ContractService;
import com.nz.entity.UserEntity;
import com.nz.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ContractService contractService;

    @GetMapping("/join01")
    public String join01() {
        return "auth/join01";
    }

    @GetMapping("/join02")
    public String join02() {
        return "auth/join02";
    }

    @PostMapping("/joinPro")
    public String joinPro(UserDTO userDTO, Model model) {
        this.userService.createUser(userDTO);
        return "auth/joinPro";
    }
    
    @GetMapping("/mypage")
    @PreAuthorize("isAuthenticated()")
    public String mypage(Principal principal, Model model) {
        String username = principal.getName();
        UserDTO userDTO = userService.readUsername(username);
        model.addAttribute("user", userDTO);
        return "user/mypage";
    }
    
    @GetMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public String userUpdateForm(Principal principal, Model model) {
    	UserDTO userDTO = this.userService.readUsername(principal.getName());
    	model.addAttribute("userDTO", userDTO);
        return "auth/userUpdateForm";
    }
    
    @PostMapping("/update")
	@PreAuthorize("isAuthenticated()")
	public String userUpdate(UserDTO userDTO) {
		this.userService.userUpdate(userDTO);
		return "redirect:/user/mypage";
	}
    
    @GetMapping("/userDelete")
	@PreAuthorize("isAuthenticated()")
	public String userDeleteForm() {
		return "userDeleteForm";
	}
	
	@PostMapping("/userDelete")
	@PreAuthorize("isAuthenticated()")
	public String userDelete(@RequestParam("password")String password, Principal principal) {
		
		this.userService.userDelete(principal.getName(), password);
		
		return "redirect:/user/logout";
	}
    
    @GetMapping("/myContract")
    @PreAuthorize("isAuthenticated()")
    public String myContract(Model model, Principal principal) {
        String currentUsername = principal.getName();
        List<ContractDTO> contractsAsLandlord = contractService.getContractsByLandlordUsername(currentUsername);
        List<ContractDTO> contractsAsTenant = contractService.getContractsByTenantUsername(currentUsername);
        model.addAttribute("contractsAsLandlord", contractsAsLandlord);
        model.addAttribute("contractsAsTenant", contractsAsTenant);
        return "user/myContract";
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam("username") String username) {
        boolean exists = userService.isUsernameTaken(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/mypage/{memberId}")
    public String mypage(@PathVariable Long memberId, Model model) {
        UserEntity user = userService.findById(memberId);
        model.addAttribute("user", user);
        return "user/mypage";
    }
}
