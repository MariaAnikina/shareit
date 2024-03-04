package ru.sber.shareit.service;

import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;

import java.util.List;

public interface UserService {
	List<UserInfoDto> getUsers();
	UserInfoDto getUsersById(Long id);
	UserDto create(UserDto userDto);
	UserDto update(Long id, UserDto userDto);
	void delete(Long id);
	Boolean existUserByUsername(String username);

	Boolean existUserByEmail(String email);
}
