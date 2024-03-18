package ru.sber.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.sber.shareit.config.SecurityConfig;
import ru.sber.shareit.controller.UserController;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.dto.user.UserInfoDto;
import ru.sber.shareit.service.UserService;
import ru.sber.shareit.util.UserValidator;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(SecurityConfig.class)
public class UserControllerTest {
	@MockBean
	private UserService userService;
	@MockBean
	private final UserValidator userValidator;
	private final MockMvc mvc;

	private final UserDto userDto = new UserDto(1L, "test1", "123", "User1",
			"test1@email.com", "ROLE_USER", "Рязань");

	private final UserInfoDto userInfoDto = new UserInfoDto(1L, "test1", "User1",
			"test1@email.com", "ROLE_USER", "Рязань");

}
