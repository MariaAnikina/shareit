package ru.sber.shareit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sber.shareit.util.group.Create;
import ru.sber.shareit.util.group.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long id;
	@NotBlank(message = "username не может быть пустым", groups = {Create.class})
	private String username;
	@NotBlank(message = "password не может быть пустым", groups = {Create.class})
	private String password;
	@NotBlank(message = "Имя пользователя не может быть пустым", groups = {Create.class})
	private String name;
	@NotNull(message = "Email пользователя не может быть пустым", groups = {Create.class})
	@NotBlank(message = "Email пользователя не может быть пустым", groups = {Create.class})
	@Email(message = "Email пользователя должен быть корректным", groups = {Create.class, Update.class})
	private String email;
	private String role;
	@NotBlank(message = "Город проживания пользователя не может быть пустым", groups = {Create.class})
	private String city;
}