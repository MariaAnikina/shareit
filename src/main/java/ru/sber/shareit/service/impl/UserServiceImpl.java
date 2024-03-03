package ru.sber.shareit.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;
import ru.sber.shareit.entity.Role;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.exception.UserAlreadyExistsException;
import ru.sber.shareit.exception.UserNotFoundException;
import ru.sber.shareit.repository.UserRepository;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.sber.shareit.util.mapper.UserMapper.*;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	private final BCryptPasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	@Override
	public List<UserInfoDto> getUsers() {
		log.info("Запрошен список пользователей");
		return userRepository.findAll().stream()
				.map(UserMapper::toUserOutDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public UserDto getUsersById(Long id) {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
		}
		User user = userOptional.get();
		log.info("Запрошен пользователь {}", user);
		return null;
	}

	@Transactional
	@Override
	public UserDto create(UserDto userDto) {
		try {
			User user = toUserNoRole(userDto);
			if (userDto.getRole() == null) {
				user.setRole(Role.ROLE_USER);
			} else {
				user.setRole(Role.valueOf(userDto.getRole()));
			}
			String encodedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
			userRepository.save(user);
			log.info("Добавлен пользователь {}", user);
			return toUserDto(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserAlreadyExistsException("Пользователь с email " + userDto.getEmail()
					+ " или username " + userDto.getUsername() + " уже существует");
		}
	}

	@Transactional
	@Override
	public UserDto update(Long id, UserDto userDto) {
		try {
			Long userId = userDto.getId();
			Optional<User> userOptional = userRepository.findById(userId);
			if (userOptional.isEmpty()) {
				throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
			}
			User oldUser = userOptional.get();
			User updateUser = toUser(userDto);

			updateUser.setUsername(oldUser.getUsername());
			updateUser.setPassword(oldUser.getPassword());
			updateUser.setEmail(oldUser.getEmail());
			updateUser.setName(oldUser.getName());
			updateUser.setName(oldUser.getName());
			updateUser.setCity(oldUser.getCity());

			User user = userRepository.save(updateUser);
			log.info("Обновлен пользователь {}", user);
			return toUserDto(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserAlreadyExistsException("Пользователь с email " + userDto.getEmail()
					+ " или username " + userDto.getUsername() + " уже существует");
		}
	}

	@Transactional
	@Override
	public void delete(Long id) {
		userRepository.deleteById(id);
		log.info("Удален пользователь с id={}", id);
	}

	@Transactional(readOnly = true)
	@Override
	public Boolean existUserByUsername(String username) {
		return userRepository.findByUsername(username).isPresent();
	}

	@Transactional(readOnly = true)
	@Override
	public Boolean existUserByEmail(String email) {
		return userRepository.findByEmail(email).isPresent();
	}
}
