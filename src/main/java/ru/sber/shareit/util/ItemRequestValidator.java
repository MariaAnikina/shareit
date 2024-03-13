package ru.sber.shareit.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.entity.ItemRequest;

@Component
public class ItemRequestValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return ItemRequestDto.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		ItemRequestDto itemRequestDto = (ItemRequestDto) o;
		if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
			errors.rejectValue("description", "", "Описание запроса не может быть пустым");
		}
	}
}
