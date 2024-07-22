package com.nz.data;

import java.security.Timestamp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	
	@Pattern(regexp = "^[a-zA-Z](?=.*\\d)[a-zA-Z0-9]{7,13}$",
			 message = "형식에 맞지 않는 아이디입니다.\\n영문 숫자를 포함하여 8~14자 내로 작성하세요.")
	@NotBlank(message = "아이디를 입력하세요.")
	private String username;
	
	@NotBlank(message = "이름을 입력하세요.")
	private String name;
	
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{9,17}$",
	 		 message = "형식에 맞지 않는 아이디입니다.\\n영문 숫자를 포함하여 10~18자 내로 작성하세요.")
	@NotBlank(message = "비밀번호를 입력하세요.")
	private String password;
	
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{9,17}$",
	 		 message = "형식에 맞지 않는 아이디입니다.\\n영문 숫자를 포함하여 8~14자 내로 작성하세요.")
	@NotBlank(message = "비밀번호 확인을 입력하세요.")
	private String password2;
	
	@Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$",
			 message = "유효한 이메일 주소를 입력하세요.")
	@NotBlank(message = "이메일 주소를 입력하세요.")
	private String email;
	
	@Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
			 message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
	@NotBlank(message = "전화번호를 입력하세요.")
	private String phoneNumber;
	
	private String accountNumber;
	
	private int verified;
	
	private Timestamp createDate;
	
	private String role;

}
