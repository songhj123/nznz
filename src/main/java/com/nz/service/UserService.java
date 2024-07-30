package com.nz.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nz.data.UserDTO;
import com.nz.entity.UserEntity;
import com.nz.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long getUserByMemberId(String username) {
    	Optional<UserEntity> userEntity = userRepository.findByUsername(username);
    	return userEntity.get().getMemberID();
    }
    
    public UserDTO getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (userEntity == null) {
            return null;
        }
        return convertToUserDTO(userEntity);
    }

    private UserDTO convertToUserDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .memberId(userEntity.getMemberID())
                .username(userEntity.getUsername())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .phoneNumber(userEntity.getPhoneNumber())
                .accountNumber(userEntity.getAccountNumber())
                .verified(userEntity.getVerified())
                .createDate(Timestamp.valueOf(userEntity.getCreateDate()))
                .role(userEntity.getRole())
                .build();
    }
    
    public void createUser(UserDTO userDTO) {
        UserEntity ue = UserEntity.builder()
                                  .name(userDTO.getName())
                                  .username(userDTO.getUsername())
                                  .password(passwordEncoder.encode(userDTO.getPassword()))
                                  .email(userDTO.getEmail())
                                  .phoneNumber(userDTO.getPhoneNumber())
                                  .accountNumber(userDTO.getAccountNumber())
                                  .verified(userDTO.getVerified())
                                  .createDate(LocalDateTime.now())
                                  .role(userDTO.getRole())
                                  .build();
        this.userRepository.save(ue);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserDTO readUsername(String username) {
        Optional<UserEntity> op = this.userRepository.findByUsername(username);
        if (op.isPresent()) {
            UserEntity ue = op.get();
            UserDTO userDTO = UserDTO.builder()
                                     .name(ue.getName())
                                     .username(ue.getUsername())
                                     .password(ue.getPassword())
                                     .email(ue.getEmail())
                                     .phoneNumber(ue.getPhoneNumber())
                                     .accountNumber(ue.getAccountNumber())
                                     .verified(ue.getVerified())
                                     .role(ue.getRole())
                                     .build();
            return userDTO;
        } else {
            throw new RuntimeException("존재하지 않는 아이디입니다.");
        }
    }


}