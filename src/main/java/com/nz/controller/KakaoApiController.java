package com.nz.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoApiController {

    private final KakaoUserService kakaoUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @GetMapping("/user/login")
    public ResponseEntity<?> callback(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
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

        // POST 요청으로 username과 password 전송
        String username = user.getUsername();
        String password = "aaaaaaaaa1";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("username", username);
        requestBody.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<>(requestBody, headers);

        // 절대 경로 사용
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("http://localhost:8080/user/login", loginRequest, String.class);

        if (loginResponse.getStatusCode() == HttpStatus.OK) {
            // 로그인 성공 시 세션 유지하기 위해 쿠키를 저장합니다.
            List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null) {
                // 쿠키를 저장하고 이후 요청에서 사용하도록 설정합니다.
                headers.put(HttpHeaders.COOKIE, cookies);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
