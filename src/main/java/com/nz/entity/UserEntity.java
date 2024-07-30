package com.nz.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@SequenceGenerator(name = "user_seq_generator",
					sequenceName = "seq_member",
					initialValue = 1,
					allocationSize = 0)
public class UserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_member")
	private Long memberID;
	
	private String name;
	
	@Column(unique = true)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	private String email;
	private String phoneNumber;
	private String accountNumber;
	private int verified;
	private LocalDateTime createDate;
	private String role;

}
