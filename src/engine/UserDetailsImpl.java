/*
 * Copyright (c) 2022. Miguel R.
 * All rights reserved.
 */

package engine;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class UserDetailsImpl implements UserDetails {
	private final List<GrantedAuthority> rolesAndAuthorities;
	private final String username;
	private final String password;

	public UserDetailsImpl(User user) {
		username = user.getEmail();
		password = user.getPassword();
		rolesAndAuthorities = List.of(new SimpleGrantedAuthority(user.getRole()));
	}

	@Override
	public List<GrantedAuthority> getAuthorities() {
		return rolesAndAuthorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	// 4 remaining methods that just return true
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
