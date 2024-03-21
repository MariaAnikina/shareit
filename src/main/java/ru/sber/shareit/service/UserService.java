package ru.sber.shareit.service;

import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;

import java.util.List;

/**
 * UserService interface
 */

public interface UserService {

	/**
	 * get all users
	 * with paging option: the size and the number of the page is defined by from/size parameters of request
	 *
	 * @param from number of the page
	 * @param size number of item requests per page
	 * @return list of users
	 */
	List<UserInfoDto> getUsers(int from, int size);

	/**
	 * get user by id
	 *
	 * @param id user's id
	 * @return user
	 */
	UserInfoDto getUserById(Long id);

	/**
	 * to add user's data (save and assign identity)
	 *
	 * @param userDto user to save and registered
	 * @return user with assigned id
	 */
	UserDto create(UserDto userDto);

	/**
	 * update user's properties
	 *
	 * @param id      user's id
	 * @param userDto user to update
	 * @return updated user
	 */
	UserDto update(Long id, UserDto userDto);

	/**
	 * delete user by id. This is only allowed to moderator or user account owner.
	 *
	 * @param id         user's id to delete
	 * @param userIdAuth authenticated user's id
	 */
	void delete(Long id, Long userIdAuth);

	/**
	 * check the existence of user with the username
	 *
	 * @param username username field of user
	 * @return true if user with the username exists
	 */
	Boolean existUserByUsername(String username);

	/**
	 * check the existence of user with the email
	 *
	 * @param email email field of user
	 * @return true if user with the email exists
	 */
	Boolean existUserByEmail(String email);
}
