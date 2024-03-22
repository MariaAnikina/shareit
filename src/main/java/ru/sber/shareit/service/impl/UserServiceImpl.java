package ru.sber.shareit.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.entity.enums.Role;
import ru.sber.shareit.exception.UserAlreadyExistsException;
import ru.sber.shareit.exception.UserNotFoundException;
import ru.sber.shareit.repository.UserRepository;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.sber.shareit.util.mapper.UserMapper.toUser;
import static ru.sber.shareit.util.mapper.UserMapper.toUserDto;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
	private final BCryptPasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	@Override
	public List<UserInfoDto> getUsers(int from, int size) {
		log.info("Запрошен список пользователей");
		return userRepository.findAll(getPageable(from, size)).stream()
				.map(UserMapper::toUserInfoDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public UserInfoDto getUserById(Long id) {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
		}
		User user = userOptional.get();
		log.info("Запрошен пользователь {}", user);
		return UserMapper.toUserInfoDto(user);
	}

	@Transactional
	@Override
	public UserDto create(UserDto userDto) {
		try {
			if (userDto.getUsername().equals("moderator")) {
				userDto.setRole("ROLE_MODERATOR");
			}
			if (userDto.getRole() == null) {
				userDto.setRole("ROLE_USER");
			}
			User user = toUser(userDto);
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
			Optional<User> userOptional = userRepository.findById(id);
			if (userOptional.isEmpty()) {
				throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
			}
			userDto.setRole(Role.ROLE_USER.toString());
			User oldUser = userOptional.get();
			User updateUser = toUser(userDto);
			updateUser(id, oldUser, updateUser);
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
	public void delete(Long id, Long userIdAuth) {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
		}
		if (id.equals(userIdAuth)) {
			userRepository.deleteById(id);
			log.info("Удален пользователь с id={}", id);
			return;
		}
		Optional<User> userOptionalAuth = userRepository.findById(userIdAuth);
		if (userOptionalAuth.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + userIdAuth + " не найден");
		}
		if (userOptionalAuth.get().getRole().equals(Role.ROLE_MODERATOR)) {
			userRepository.deleteById(id);
			log.info("Удален пользователь с id={}", id);
		}
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

	private void updateUser(Long id, User oldUser, User updateUser) {
		updateUser.setId(id);
		if (updateUser.getUsername() == null || updateUser.getUsername().isBlank()) {
			updateUser.setUsername(oldUser.getUsername());
		}
		if (updateUser.getPassword() == null || updateUser.getPassword().isBlank()) {
			updateUser.setPassword(oldUser.getPassword());
		} else {
			String encodedPassword = passwordEncoder.encode(updateUser.getPassword());
			updateUser.setPassword(encodedPassword);
		}
		if (updateUser.getEmail() == null || updateUser.getEmail().isBlank()) {
			updateUser.setEmail(oldUser.getEmail());
		}
		if (updateUser.getName() == null || updateUser.getName().isBlank()) {
			updateUser.setName(oldUser.getName());
		}
		if (updateUser.getCity() == null || updateUser.getCity().isBlank()) {
			updateUser.setCity(oldUser.getCity());
		}
	}

	private Pageable getPageable(int from, int size) {
		int page = from / size;
		return PageRequest.of(page, size);
	}
}
