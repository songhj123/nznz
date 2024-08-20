package com.nz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nz.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
	
	Optional<UserEntity> findByUsername(String username);
	
	boolean existsByUsername(String username);
	
	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByKakaoId(Long kakaoId);

	Optional<UserEntity> findById(Long memberId);
	
	Page<UserEntity> findByRole(String role, Pageable pageable);
	
	List<UserEntity> findAllByUsernameIn(List<String> usernames);


}
