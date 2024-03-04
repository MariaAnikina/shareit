package ru.sber.shareit.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.UserValidator;
import ru.sber.shareit.util.group.Update;

import javax.validation.Valid;

@AllArgsConstructor
@RequestMapping("/users")
@Controller
public class UserController {
	private final UserService userService;
	private final UserValidator userValidator;

	@GetMapping("/{userId}")
	public String getUserById(@PathVariable long userId, Model model) {
		model.addAttribute("user", userService.getUsersById(userId));
		return "users/get-user";
	}

	@GetMapping
	public String getUsers(Model model) {
		model.addAttribute("users", userService.getUsers());
		return "users/get-users";
	}

	@GetMapping("/create")
	public String createUserPage(@ModelAttribute("user")UserDto userDto) {
		return "users/create-user";
	}


	@PostMapping("/create")
	public String performCreateUser(@ModelAttribute("user") UserDto userDto,
	                                  BindingResult bindingResult) {
		userValidator.validate(userDto, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/auth/registration";
		}
		userService.create(userDto);
		return "redirect:/users";
	}

	@GetMapping("/update/{userId}")
	public String updateUserPage(@PathVariable long userId, Model model) {
		System.out.println(userId);
		model.addAttribute("userId", userId);
		model.addAttribute("user", new UserDto());
		return "users/update-user";
	}


	@Validated(Update.class)
	@PutMapping("/update/{userId}")
	public String performUpdateUser(@ModelAttribute("user") UserDto userDto,
	                                BindingResult bindingResult, @PathVariable long userId) {
		System.out.println(userId);
		userValidator.validate(userDto, bindingResult);
		if (bindingResult.hasErrors()) {
			return "users/update-user";
		}
		userService.update(userId, userDto);
		return "redirect:/users";
	}

	@DeleteMapping("/delete/{userId}")
	public String deleteUser(@PathVariable long userId) {
		userService.delete(userId);
		return "auth/login";
	}
}
