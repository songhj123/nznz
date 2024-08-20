package com.nz.security;

import lombok.Getter;

@Getter
public enum UserRole {

	ADMIN("ROLE_MANAGER"), USER("ROLE_USER"), SUSPENDED("ROLE_SUSPENDED");
	
	private String value;
	UserRole(String value){
		this.value = value;
	}
}
