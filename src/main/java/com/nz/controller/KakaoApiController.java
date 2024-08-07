package com.nz.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.nz.data.KakaoUserInfoResponseDTO;
import com.nz.entity.UserEntity;
import com.nz.repository.UserRepository;
import com.nz.service.KakaoUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class KakaoApiController {

    private final KakaoUserService kakaoUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

	@Value("${kakao.client_id}")
	private String client_id;

    @GetMapping("/kakao/login")
    public String callback(@RequestParam("code") String code, HttpServletRequest request, Model model) throws IOException {
        String accessToken = kakaoUserService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDTO userInfo = kakaoUserService.getUserInfo(accessToken);

        // 액세스 토큰을 세션에 저장
        request.getSession().setAttribute("kakaoAccessToken", accessToken);
        
        Optional<UserEntity> optionalUser = userRepository.findByKakaoId(userInfo.getId());

        UserEntity user;
        if (optionalUser.isEmpty()) {
            user = UserEntity.builder()
                             .kakaoId(userInfo.getId())
                             .username("KAKAO_" + userInfo.getId())
                             .password(passwordEncoder.encode("aaaaaaaaa1"))
                             .name(userInfo.getKakaoAccount().getProfile().getNickName())
                             .role("ROLE_USER")
                             .createDate(LocalDateTime.now())
                             .build();
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }


        model.addAttribute("username", user.getUsername());
        model.addAttribute("password", "aaaaaaaaa1");

        return "auth/kakaoLogin";
    }
    
    @GetMapping("/user/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 세션에서 액세스 토큰 가져오기
        String accessToken = (String) request.getSession().getAttribute("kakaoAccessToken");
        if (accessToken != null) {
            // 카카오 로그아웃 요청 보내기 (클라이언트에서 직접 처리)
            String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + client_id + "&logout_redirect_uri=http://localhost:8080/kakao/logout";
            response.sendRedirect(kakaoLogoutUrl);
        } else {
            // 액세스 토큰이 없는 경우, 애플리케이션 로그아웃만 처리
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            }
            response.sendRedirect("/home");
        }
    }

    @GetMapping("/kakao/logout")
    public String logoutCallback(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 애플리케이션 세션 로그아웃 처리
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/home";
    }



}
