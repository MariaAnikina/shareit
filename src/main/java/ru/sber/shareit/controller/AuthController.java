package ru.sber.shareit.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sber.shareit.dto.user.UserAuthDto;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.security.UserDetailsImpl;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.UserValidator;
import ru.sber.shareit.util.group.Create;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
@Validated
public class AuthController {
	private final UserValidator userValidator;
	private final UserService service;

	@GetMapping("/login")
	public String loginPage(@ModelAttribute("user") UserAuthDto userDto) {
		Long userId = getUserId();
		if (userId != null) {
			return "redirect:/items/city";		}
		return "auth/login";
	}

	@GetMapping("/registration")
	public String registrationPage(@Valid @ModelAttribute("user") UserDto userDto) {
		Long userId = getUserId();
		if (userId != null) {
			return "redirect:/items/city";
		}
		return "auth/registration";
	}

	@Validated(Create.class)
	@PostMapping("/registration")
	public String performRegistration(@Valid @ModelAttribute("user") UserDto userDto,
	                                  BindingResult bindingResult) {
//		userValidator.validate(userDto, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/auth/registration";
		}
		service.create(userDto);
		return "redirect:/auth/login";
	}

	private Long getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userId = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			userId = userDetails.getUser().getId();
		}
		return userId;
	}
}
