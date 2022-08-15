/*
 * Copyright (c) 2022. Miguel R.
 * All rights reserved.
 */

package engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(name = "user")
public class User {
	@NotNull
	@Id
	@Column(nullable = false)
	@Email
	private String email; //email address
	private String password; //password
	private String role;

	public User() {

	}

	@JsonCreator
	public User(@JsonProperty("email") String email,
	            @JsonProperty("password") String password) {
		Pattern emailPattern = Pattern.compile(".*\\w+@.*\\w+\\.\\w+");
		Matcher matcher = emailPattern.matcher(email);
//		System.out.println("matcher = " + matcher);
//		System.out.println("email = " + email);
//		System.out.println(matcher.matches());
		if (password.length() < 5) {
			System.out.println("[ERROR] creating user: password too short " + password);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		if (!matcher.matches()) {
			System.out.println("[ERROR] creating user: not email " + email);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		this.email = email;
		this.password = password;
		this.role = "ROLE_USER";
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User{" +
				"email='" + email + '\'' +
				", password='" + password + '\'' +
				'}';
	}

	public String getRole() {
		return role;
	}
}
