package com.nz.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class KakaoLoginController {
	
	@Value("${kakao.client_id}")
	private String client_id;
	
	@Value("${kakao.redirect_uri}")
	private String redirect_uri;
	
	@GetMapping("/loginForm")
	private String loginPage(Model model) {
		String location = "http://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri;
		model.addAttribute("location", location);
		
		return "auth/loginForm";
	}
}