package ru.sber.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.entity.enams.Role;
import ru.sber.shareit.exception.UserAlreadyExistsException;
import ru.sber.shareit.exception.UserNotFoundException;
import ru.sber.shareit.repository.UserRepository;
import ru.sber.shareit.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static ru.sber.shareit.util.mapper.UserMapper.toUserDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	private User user;

	@BeforeEach
	public void setUp() {
		user = new User(1L, "user1", "123", "User1",
				"user1@email.com", Role.ROLE_USER, "Рязань");
	}

	@Test
	public void getUsersByIdTest_whenUserFound_thenReturnUserInfoDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));

		UserInfoDto userDtoOutgoing = userService.getUserById(1L);

		assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
		assertThat(userDtoOutgoing.getUsername(), equalTo(user.getUsername()));
		assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
		assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
		assertThat(userDtoOutgoing.getRole(), equalTo(user.getRole().toString()));
		assertThat(userDtoOutgoing.getCity(), equalTo(user.getCity()));
	}

	@Test
	public void getUsersByIdTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		UserNotFoundException e = Assertions.assertThrows(
				UserNotFoundException.class,
				() -> userService.getUserById(1L)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void getUsersTest_whenUsersFound_thenReturnUsersInfoDto() {
		Mockito
				.when(userRepository.findAll(PageRequest.of(0, 10)))
				.thenReturn(new PageImpl<>(List.of(user)));
		List<UserInfoDto> users = userService.getUsers(0, 10);

		UserInfoDto userDtoOutgoing = users.get(0);

		assertThat(users.size(), equalTo(1));
		assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
		assertThat(userDtoOutgoing.getUsername(), equalTo(user.getUsername()));
		assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
		assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
		assertThat(userDtoOutgoing.getRole(), equalTo(user.getRole().toString()));
		assertThat(userDtoOutgoing.getCity(), equalTo(user.getCity()));
	}

	@Test
	public void createTest_whenUserCreate_thenReturnUserDto() {
		Mockito
				.when(userRepository.save(any(User.class)))
				.then(returnsFirstArg());
		Mockito
				.when(passwordEncoder.encode(anyString()))
				.thenReturn("123шифр");
		UserDto userDtoOutgoing = userService.create(toUserDto(user));

		assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
		assertThat(userDtoOutgoing.getUsername(), equalTo(user.getUsername()));
		assertThat(userDtoOutgoing.getPassword(), equalTo(user.getPassword() + "шифр"));
		assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
		assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
		assertThat(userDtoOutgoing.getRole(), equalTo(user.getRole().toString()));
		assertThat(userDtoOutgoing.getCity(), equalTo(user.getCity()));
	}

	@Test
	public void createTest_whenEmailOrUsernameAlreadyExists_thenReturnDataIntegrityViolationException() {
		Mockito
				.when(userRepository.save(any(User.class)))
				.thenThrow(DataIntegrityViolationException.class);

		UserAlreadyExistsException e = Assertions.assertThrows(
				UserAlreadyExistsException.class,
				() -> userService.create(toUserDto(user))
		);

		assertThat(e.getMessage(), equalTo("Пользователь с email user1@email.com или username user1 уже существует"));
	}

	@Test
	public void updateTest_whenUserUpdate_thenReturnUserDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(userRepository.save(any(User.class)))
				.then(returnsFirstArg());

		UserDto userDtoOutgoing = userService.update(user.getId(), toUserDto(user));

		assertThat(userDtoOutgoing.getId(), equalTo(user.getId()));
		assertThat(userDtoOutgoing.getUsername(), equalTo(user.getUsername()));
		assertThat(userDtoOutgoing.getName(), equalTo(user.getName()));
		assertThat(userDtoOutgoing.getEmail(), equalTo(user.getEmail()));
		assertThat(userDtoOutgoing.getRole(), equalTo(user.getRole().toString()));
		assertThat(userDtoOutgoing.getCity(), equalTo(user.getCity()));
	}

	@Test
	public void updateTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		UserNotFoundException e = Assertions.assertThrows(
				UserNotFoundException.class,
				() -> userService.update(user.getId(), toUserDto(user))
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void updateTest_whenEmailOrUsernameAlreadyExists_thenReturnDataIntegrityViolationException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(userRepository.save(any(User.class)))
				.thenThrow(DataIntegrityViolationException.class);

		UserAlreadyExistsException e = Assertions.assertThrows(
				UserAlreadyExistsException.class,
				() -> userService.update(user.getId(), toUserDto(user))
		);

		assertThat(e.getMessage(), equalTo("Пользователь с email user1@email.com или username user1 уже существует"));
	}

	@Test
	public void deleteTest_whenUserDelete_thenReturnVoid() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));

		userService.delete(1L, 1L);

		Mockito.verify(userRepository).deleteById(anyLong());
	}
}
