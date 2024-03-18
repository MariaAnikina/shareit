package ru.sber.shareit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDto {
	@NotBlank(message = "username не может быть пустым")
	private String username;
	@NotBlank(message = "password не может быть пустым")
	private String password;
}
