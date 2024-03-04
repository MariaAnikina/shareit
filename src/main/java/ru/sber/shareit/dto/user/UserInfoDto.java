package ru.sber.shareit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserInfoDto {
	private Long id;
	private String username;
	private String name;
	private String email;
	private String city;
}

