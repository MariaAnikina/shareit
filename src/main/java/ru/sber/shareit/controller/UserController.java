package ru.sber.shareit.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.security.UserDetailsImpl;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.UserValidator;
import ru.sber.shareit.util.group.Create;
import ru.sber.shareit.util.group.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@AllArgsConstructor
@RequestMapping("/users")
@Controller
public class UserController {
	private final UserService userService;
	private final UserValidator userValidator;

	@GetMapping("/{userId}")
	public String getUserById(@PathVariable Long userId, Model model) {
		Long userIdAuth = getUserId();
		model.addAttribute("userId", userIdAuth);
		model.addAttribute("user", userService.getUserById(userId));
		return "users/get-user";
	}

	@GetMapping
	public String getUsers(Model model, @RequestParam(defaultValue = "0") @PositiveOrZero int from,
	@RequestParam(defaultValue = "3") @Positive int size) {
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("users", userService.getUsers(from, size));
		return "users/get-users";
	}

	@GetMapping("/create")
	public String createUserPage(@ModelAttribute("user")UserDto userDto) {
		return "users/create-user";
	}

	@PostMapping("/create")
	public String performCreateUser(@Valid @ModelAttribute("user") UserDto userDto,
	                                  BindingResult bindingResult) {
//		userValidator.validate(userDto, bindingResult);
		if (bindingResult.hasErrors()) {
			return "users/create-user";
		}
		userService.create(userDto);
		return "redirect:/users";
	}

	@GetMapping("/update")
	public String updateUserPage(Model model) {
		model.addAttribute("user", new UserDto());
		return "users/update-user";
	}


	@Validated(Update.class)
	@PutMapping("/update")
	public String performUpdateUser(@Valid @ModelAttribute("user") UserDto userDto,
	                                BindingResult bindingResult) {
		Long userId = getUserId();
		userValidator.validate(userDto, bindingResult);
		if (bindingResult.hasErrors()) {
			return "users/update-user";
		}
		userService.update(userId, userDto);
		return "redirect:/users";
	}

	@DeleteMapping("/delete/{userId}")
	public String deleteUser(@PathVariable Long userId) {
		Long userIdAuth = getUserId();
		userService.delete(userId, userIdAuth);
		return "auth/login";
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
