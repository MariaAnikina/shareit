package ru.sber.shareit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserInfoDto {
	private Long id; //id оставила для фронта
	private String username;
	private String name;
	private String email;
	private String role; //юзер не должен знать о роли, оставила в рамках дебага
	private String city;
}

