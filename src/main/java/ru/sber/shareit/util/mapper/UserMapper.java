package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.entity.enums.Role;

public class UserMapper {
	public static UserInfoDto toUserInfoDto(User user) {
		return new UserInfoDto(
				user.getId(),
				user.getUsername(),
				user.getName(),
				user.getEmail(),
				user.getRole().toString(),
				user.getCity()
		);
	}

	public static User toUser(UserDto userDto) {
		return new User(
				userDto.getId(),
				userDto.getUsername(),
				userDto.getPassword(),
				userDto.getName(),
				userDto.getEmail(),
				Role.valueOf(userDto.getRole()),
				userDto.getCity()
		);
	}

	public static UserDto toUserDto(User user) {
		return new UserDto(
				user.getId(),
				user.getUsername(),
				user.getPassword(),
				user.getName(),
				user.getEmail(),
				user.getRole().toString(),
				user.getCity()
		);
	}
}