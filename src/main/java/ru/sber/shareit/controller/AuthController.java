package ru.sber.shareit.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sber.shareit.dto.user.UserAuthDto;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.UserIdUtil;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthController {
	private final UserService service;
	private final UserIdUtil userIdUtil;

	@GetMapping("/login")
	public String loginPage(@ModelAttribute("user") UserAuthDto userDto) {
		Long userId = userIdUtil.getUserId();
		if (userId != null) {
			return "redirect:/items/city";
		}
		return "auth/login";
	}

	@GetMapping("/registration")
	public String registrationPage(@ModelAttribute("user") UserDto userDto) {
		Long userId = userIdUtil.getUserId();
		if (userId != null) {
			return "redirect:/items/city";
		}
		return "auth/registration";
	}

	@PostMapping("/registration")
	public String performRegistration(@Valid @ModelAttribute("user") UserDto userDto,
	                                  BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "/auth/registration";
		}
		service.create(userDto);
		return "redirect:/auth/login";
	}
}
