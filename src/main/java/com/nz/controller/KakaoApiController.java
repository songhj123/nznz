package com.nz.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.Optional;


import org.springframework.security.crypto.password.PasswordEncoder;
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

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class KakaoApiController {

    private final KakaoUserService kakaoUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/kakao/login")
    public String callback(@RequestParam("code") String code, Model model) throws IOException {
        String accessToken = kakaoUserService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDTO userInfo = kakaoUserService.getUserInfo(accessToken);

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
}
