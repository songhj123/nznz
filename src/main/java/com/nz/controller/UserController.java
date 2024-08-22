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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		return "auth/userDeleteForm";
	}
	
	@PostMapping("/userDelete")
	@PreAuthorize("isAuthenticated()")
	public String userDelete(@RequestParam("password")String password, Principal principal
			,@RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
		if(!password.equals(confirmPassword)) {
	        redirectAttributes.addFlashAttribute("error", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
			return "redirect:/user/userDelete";
		}
		
	    int ch = this.userService.userDelete(principal.getName(), password);
	    if(ch==1) {	
	        redirectAttributes.addFlashAttribute("success", true);
	    }else {
	        redirectAttributes.addFlashAttribute("error", "비밀번호가 틀렸습니다.");
	    }
		return "redirect:/user/userDelete";
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
    
 // 아이디 찾기 페이지
    @GetMapping("/findId")
    public String showFindIdPage() {
        return "auth/findId";  // findId.html로 이동
    }

    // 아이디 찾기 처리   
    @PostMapping("/findIdResult")
    public String findId(@RequestParam("email") String email, RedirectAttributes redirectAttributes,Model model) {
        try {
            String username = userService.findUsernameByEmail(email);
            model.addAttribute("username", username);
            return "auth/findIdResult";  // 성공 시 결과 페이지로 리다이렉트
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/findId";  // 실패 시 아이디 찾기 페이지로 리다이렉트
        }
    }

    // 비밀번호 재설정 페이지
    @GetMapping("/resetPassword")
    public String showResetPasswordPage() {
        return "auth/resetPassword";  // resetPassword.html로 이동
    }

    // 비밀번호 재설정 처리
    @PostMapping("/resetPasswordResult")
    public String resetPassword(@RequestParam("username") String username,
                                @RequestParam("email") String email,
                                Model model) {
        try {
            String tempPassword = userService.resetPassword(username, email);
            model.addAttribute("tempPassword", tempPassword);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "auth/resetPasswordResult";  // resetPasswordResult.html로 이동
    }
    
 // 비밀번호 변경 페이지
    @GetMapping("/changePassword")
    @PreAuthorize("isAuthenticated()")
    public String showChangePasswordPage() {
        return "auth/changePassword";  // changePassword.html로 이동
    }

    // 비밀번호 변경 처리
    @PostMapping("/changePassword")
    @PreAuthorize("isAuthenticated()")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal, Model model) {
        try {
            String username = principal.getName();
            userService.changePassword(username, oldPassword, newPassword);
            model.addAttribute("message", "비밀번호가 변경되었습니다.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "auth/changePasswordResult";  // changePasswordResult.html로 이동
    }
    
    // 비로그인 처리
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/loginForm"; // loginForm.html 뷰를 반환
    }

}
