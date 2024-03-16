package ru.sber.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.sber.shareit.config.SecurityConfig;
import ru.sber.shareit.controller.UserController;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.service.UserService;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(SecurityConfig.class)
public class UserControllerTest {
	@MockBean
	private UserService userService;
	private final ObjectMapper mapper;
	private final MockMvc mvc;

	private final UserDto userDto = new UserDto(1L,  "user1", "123", "User1",
			"user1@email.com", "ROLE_USER", "Рязань");
}
