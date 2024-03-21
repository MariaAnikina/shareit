package ru.sber.shareit.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import ru.sber.shareit.security.AddUserIdHeaderFilter;
import ru.sber.shareit.security.UserDetailsImpl;

@EnableWebSecurity
@Getter
@Setter
@AllArgsConstructor
public class SecurityConfig {
	private AddUserIdHeaderFilter addUserIdHeaderFilter;
	private UserDetailsImpl userDetails;

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/auth/login", "/error", "/auth/registration").permitAll()
				.antMatchers("/users/create", "/users").hasRole("MODERATOR")
				.anyRequest().authenticated()
				.and()
				.addFilterAfter(addUserIdHeaderFilter, UsernamePasswordAuthenticationFilter.class)
				.formLogin().loginPage("/auth/login")
				.loginProcessingUrl("/process_login")
				.defaultSuccessUrl("/items/city", true)
				.failureUrl("/auth/login?error")
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/auth/login");
		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter();
	}
}
