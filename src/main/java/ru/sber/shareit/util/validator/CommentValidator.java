package ru.sber.shareit.util.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sber.shareit.dto.item.CommentDto;

@Component
public class CommentValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return CommentDto.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		CommentDto commentDto = (CommentDto) o;
		if (commentDto.getText() == null || commentDto.getText().isBlank()) {
			errors.rejectValue("text", "", "Комментарий не может быть пустым");
		}
	}
}
