package com.sk.rest.webservices.restfulwebservices.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfiguration {

	/*
	 * Here we are overriding default SecurityChain provided by Spring Security
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		//all request to be authenticated
		http.authorizeHttpRequests(
				auth -> auth.anyRequest().authenticated()
				);
		
		//enable basic authentication
		http.httpBasic(withDefaults()); //WithDefaults() method is present in  Customizer class of Spring Security
		
		//diable csrf
		http.csrf().disable();
		
		return http.build();
	}
	
}
