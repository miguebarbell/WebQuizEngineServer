/*
 * Copyright (c) 2022. Miguel R.
 * All rights reserved.
 */

package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication()
		    .withUser("test@google.com")
		    .password("qwerty")
		    .roles("USER");

		auth.inMemoryAuthentication()
		    .withUser("user@google.com")
		    .password("12345")
		    .roles("USER");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.userDetailsService(userDetailsService) // user store 1
				.passwordEncoder(getEncoder());

//		auth
//				.inMemoryAuthentication() // user store 2
//				.withUser("Admin").password("hardcoded").roles("USER")
//				.and().passwordEncoder(NoOpPasswordEncoder.getInstance());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .mvcMatchers("/api/quizzes/**").hasAnyRole("ADMIN", "USER")
//        .mvcMatchers("/api/quizzes/**").authenticated()
//        .anyRequest().authenticated()
//        .anyRequest().permitAll() // make remaining endpoints public (including POST /register)
        .and()
        .csrf().disable() // disabling CSRF will allow sending POST request using Postman
        .httpBasic(); // enables basic auth.
	}

	@Bean
	public PasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
}
