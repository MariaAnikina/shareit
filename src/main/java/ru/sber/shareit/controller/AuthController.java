package ru.sber.shareit.controller;


import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.security.JwtGeneration;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.service.impl.UserDetailsServiceImpl;
import ru.sber.shareit.util.UserValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {

	private final UserDetailsServiceImpl userDetailsService;
	private final JwtGeneration jwtGeneration;
	private final AuthenticationManager authenticationManager;
	private final UserValidator userValidator;
	private final UserService service;

	@GetMapping("/login")
	public String loginPage(@ModelAttribute("user")UserDto userDto) {
		return "auth/login";
	}

	@PostMapping("/login_jwt")
	public String createJwtToken(@ModelAttribute("user")UserDto userDto, HttpServletResponse response) {
		UsernamePasswordAuthenticationToken authInputToken =
				new UsernamePasswordAuthenticationToken(userDto.getUsername(),
						userDto.getPassword());
		authenticationManager.authenticate(authInputToken);
		String token = jwtGeneration.generationToken(userDto.getUsername());
		response.addHeader("Authorization", "Bearer " + token);
		return "redirect:/auth/h";
	}


	@GetMapping("/registration")
	public String registrationPage(@ModelAttribute("user")UserDto userDto) {
		return "auth/registration";
	}


	@PostMapping("/registration")
	public String performRegistration(@ModelAttribute("user") UserDto userDto,
	                                  BindingResult bindingResult) {
		userValidator.validate(userDto, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/auth/registration";
		}
		service.create(userDto);
		return "redirect:/auth/login";
	}

	//метод для тестирования
	@GetMapping("/h")
	public String h() {
		return "auth/h";
	}
}
