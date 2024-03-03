package ru.sber.shareit.dto.user;

import javax.validation.constraints.NotBlank;

public class UserAuthDto {
	@NotBlank(message = "username не может быть пустым")
	private String username;
	@NotBlank(message = "password не может быть пустым")
	private String password;
}
