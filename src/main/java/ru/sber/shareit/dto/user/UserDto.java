package ru.sber.shareit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long id;
	@NotBlank(message = "username не может быть пустым")
	private String username;
	@NotBlank(message = "password не может быть пустым")
	private String password;
	@NotBlank(message = "Имя пользователя не может быть пустым")
	private String name;
	@NotNull(message = "Email пользователя не может быть пустым")
	@NotBlank(message = "Email пользователя не может быть пустым")
	@Email(message = "Email пользователя должен быть корректным")
	private String email;
	private String role;
	@NotBlank(message = "Город проживания пользователя не может быть пустым")
	private String city;
}