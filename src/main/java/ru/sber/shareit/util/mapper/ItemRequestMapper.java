package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.dto.request.ItemRequestOutDto;
import ru.sber.shareit.entity.ItemRequest;
import ru.sber.shareit.entity.User;

import java.util.List;

public class ItemRequestMapper {
	public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
		return new ItemRequestDto(
				itemRequest.getId(),
				itemRequest.getDescription(),
				itemRequest.getCreated()
		);
	}

	public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
		return new ItemRequest(
				itemRequestDto.getId(),
				itemRequestDto.getDescription(),
				user,
				itemRequestDto.getCreated()
		);
	}

	public static ItemRequestOutDto toItemRequestOutDto(ItemRequest itemRequest, List<ItemDto> items) {
		return new ItemRequestOutDto(
				itemRequest.getId(),
				itemRequest.getDescription(),
				itemRequest.getCreated(),
				items
		);
	}
}
