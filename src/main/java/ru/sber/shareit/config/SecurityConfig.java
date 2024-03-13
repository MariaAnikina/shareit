package ru.sber.shareit.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.sber.shareit.security.AddUserIdHeaderFilter;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
	private AddUserIdHeaderFilter addUserIdHeaderFilter;

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/auth/login", "/error", "/auth/registration").permitAll()
				.anyRequest().authenticated()
				.and()
				.addFilterAfter(addUserIdHeaderFilter, UsernamePasswordAuthenticationFilter.class)
				.formLogin().loginPage("/auth/login")
				.loginProcessingUrl("/process_login")
				.defaultSuccessUrl("/users", true)
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
}
