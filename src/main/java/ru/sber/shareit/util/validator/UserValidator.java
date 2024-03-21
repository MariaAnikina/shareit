package ru.sber.shareit.util.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.service.UserService;

@AllArgsConstructor
@Component
public class UserValidator implements Validator {

	private UserService service;

	@Override
	public boolean supports(Class<?> clazz) {
		return UserDto.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		UserDto userDto = (UserDto) o;
		if (service.existUserByUsername(userDto.getUsername())) {
			errors.rejectValue("username", "", "Человек с таким логином уже существует");
		}
		if (service.existUserByEmail(userDto.getEmail())) {
			errors.rejectValue("email", "", "Человек с таким email уже существует");
		}
	}
}
