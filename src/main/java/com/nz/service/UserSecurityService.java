package com.nz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nz.entity.UserEntity;
import com.nz.repository.UserRepository;
import com.nz.security.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        UserEntity userEntity = optionalUser.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("ROLE_MANAGER".equals(userEntity.getRole())){
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }

        // 기존에 계정 잠금 여부를 판단하는 메서드가 호출되도록 User 객체를 반환
        return new User(userEntity.getUsername(), userEntity.getPassword(), userEntity.isEnabled(),
                        userEntity.isAccountNonExpired(), userEntity.isCredentialsNonExpired(),
                        userEntity.isAccountNonLocked(), authorities);
    }
}
