package ru.sber.shareit.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sber.shareit.dto.item.ItemDto;

@Component
public class ItemValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return ItemDto.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		ItemDto itemDto = (ItemDto) o;
		if ((itemDto.getName() == null || itemDto.getName().isBlank()) && itemDto.getRequestId() == null) {
			errors.rejectValue("name", "", "Название вещи не должно быть пустым");
		}
		if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
			errors.rejectValue("description", "", "Описание вещи не должно быть пустым");
		}
	}
}
