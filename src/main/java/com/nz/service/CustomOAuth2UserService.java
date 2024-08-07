package com.nz.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.nz.entity.UserEntity;
import com.nz.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // 카카오에서 받은 사용자 정보 처리
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Long kakaoId = (Long) attributes.get("id");
        
        // 기존 사용자 찾기 또는 새로 생성
        UserEntity userEntity = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setKakaoId(kakaoId);
                    newUser.setUsername("Kakao_" + kakaoId); // 사용자 이름 설정
                    newUser.setPassword(""); // 비밀번호는 공백으로 처리
                    return userRepository.save(newUser);
                });
        
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes, "id");
    }
}