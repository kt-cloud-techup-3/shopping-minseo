package com.kt.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class CurrentUser extends User {
	//jwt파싱해서 넣어주면 될 것 같음
	private Long id;

	public CurrentUser(String username, String password,
		Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
}
