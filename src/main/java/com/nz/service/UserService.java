package com.nz.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nz.data.UserDTO;
import com.nz.entity.UserEntity;
import com.nz.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	@Autowired
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
                                  .role("ROLE_USER")
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
                                     .memberId(ue.getMemberID())
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
    
    public UserEntity findById(Long memberId) {
        return userRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    
    public String findUsernameByEmail(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            return userEntity.get().getUsername();
        } else {
            throw new RuntimeException("해당 이메일로 가입된 아이디가 없습니다.");
        }
    }
    
    public String resetPassword(String username, String email) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            if (user.getEmail().equals(email)) {
                // 임시 비밀번호 생성
                String tempPassword = generateTemporaryPassword();
                user.setPassword(passwordEncoder.encode(tempPassword));
                userRepository.save(user);
                return tempPassword; // 또는 이메일로 전송
            } else {
                throw new RuntimeException("이메일이 일치하지 않습니다.");
            }
        } else {
            throw new RuntimeException("존재하지 않는 아이디입니다.");
        }
    }

    private String generateTemporaryPassword() {
        // 랜덤 임시 비밀번호 생성 로직 (예: 8자리 알파벳+숫자 조합)
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    public void userUpdate(UserDTO userDTO) {
		UserEntity ue = this.userRepository.findByUsername(userDTO.getUsername()).get();
		ue.setName(userDTO.getName());
		ue.setEmail(userDTO.getEmail());
		ue.setPhoneNumber(userDTO.getPhoneNumber());
		ue.setAccountNumber(userDTO.getAccountNumber());
		this.userRepository.save(ue);
	}
	
	public int userDelete(String username, String password) {
		UserEntity ue = this.userRepository.findByUsername(username).get();
		int ch = 0;
		if(passwordEncoder.matches(password, ue.getPassword())) {
			this.userRepository.delete(ue);
			ch = 1;
		}
		return ch;
	}

    // 모든 회원을 페이징 처리하여 가져오는 메서드
    public Page<UserDTO> getAllMemberPaged(Pageable pageable) {
        // UserEntity를 Page<UserEntity>로 가져옴
        Page<UserEntity> userEntities = userRepository.findAll(pageable);
        
        // UserEntity를 UserDTO로 변환하여 Page<UserDTO>로 반환
        return userEntities.map(this::convertToUserDTO);
    }

    // 역할에 따른 회원을 페이징 처리하여 가져오는 메서드
    public Page<UserDTO> getUsersByRolePaged(String role, Pageable pageable) {
        // UserEntity를 UserDTO로 변환하여 Page<UserDTO>로 반환
        return userRepository.findByRole(role, pageable)
                             .map(this::convertToUserDTO);
    }
    
    public void updateUserRoles(List<String> usernames, String newRole) {
        List<UserEntity> users = userRepository.findAllByUsernameIn(usernames);
        for (UserEntity user : users) {
            user.setRole(newRole);
            userRepository.save(user);
        }
    }
    
    public void changePassword(String username, String oldPassword, String newPassword) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("이전 비밀번호가 맞지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}