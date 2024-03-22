package ru.sber.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.sber.shareit.controller.UserController;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.entity.enums.Role;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.UserIdUtil;
import ru.sber.shareit.util.validator.UserValidator;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
	@MockBean
	private final UserService userService;
	@MockBean
	private final UserValidator userValidator;
	@MockBean
	private final UserIdUtil userIdUtil;

	private final MockMvc mvc;

	private final UserInfoDto userInfoDto = new UserInfoDto(1L, "test1", "User1",
			"test1@email.com", "ROLE_USER", "Рязань");

	@SneakyThrows
	@Test
	public void getUserByIdTest_whenUserFound_thenReturnViewGetUser() {
		Mockito
				.when(userService.getUserById(anyLong()))
				.thenReturn(userInfoDto);

		mvc.perform(get("/users/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("users/get-user"))
				.andExpect(model().attribute("user", userInfoDto))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	public void getUsersTest_whenUsersFound_thenReturnViewGetUsers() {
		List<UserInfoDto> users = List.of(userInfoDto);
		Mockito
				.when(userService.getUsers(anyInt(), anyInt()))
				.thenReturn(users);

		mvc.perform(get("/users")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("users/get-users"))
				.andExpect(model().attribute("users", users))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	public void createUserPageTest_whenPageFound_thenReturnViewCreateUser() {
		mvc.perform(get("/users/create")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("users/create-user"))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	public void performCreateUserTest_whenUserValid_thenReturnViewCreateUser() {
		mvc.perform(post("/users/create")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("users/create-user"))
				.andExpect(status().isOk());
	}
	@SneakyThrows
	@Test
	public void updateUserPageTest_whenPageFound_thenReturnViewUpdateUser() {
		mvc.perform(get("/users/update")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("users/update-user"))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	public void performUpdateUserTest_whenUserValid_thenReturnViewGetItems() {
		mvc.perform(put("/users/update")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("redirect:/items/city"))
				.andExpect(status().is(302));
	}
}
