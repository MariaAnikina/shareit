package ru.sber.shareit.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.UserIdUtil;
import ru.sber.shareit.util.validator.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@AllArgsConstructor
@RequestMapping("/users")
@Controller
public class UserController {
	private final UserService userService;
	private final UserValidator userValidator;
	private final UserIdUtil userIdUtil;

	@GetMapping("/{userId}")
	public String getUserById(@PathVariable Long userId, Model model) {
		Long userIdAuth = userIdUtil.getUserId();
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
	public String createUserPage(@ModelAttribute("user") UserDto userDto) {
		return "users/create-user";
	}

	@PostMapping("/create")
	public String performCreateUser(@Valid @ModelAttribute("user") UserDto userDto,
	                                BindingResult bindingResult) {
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

	@PutMapping("/update")
	public String performUpdateUser(@ModelAttribute("user") UserDto userDto,
	                                BindingResult bindingResult) {
		Long userId = userIdUtil.getUserId();
		userValidator.validate(userDto, bindingResult);
		if (bindingResult.hasErrors()) {
			return "users/update-user";
		}
		userService.update(userId, userDto);
		return "redirect:/items/city";
	}

	@DeleteMapping("/delete/{userId}")
	public String deleteUser(@PathVariable Long userId, HttpServletRequest request) {
		Long userIdAuth = userIdUtil.getUserId();
		userService.delete(userId, userIdAuth);
		request.getSession().invalidate();
		return "auth/login";
	}
}
