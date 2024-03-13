package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.entity.ItemRequest;
import ru.sber.shareit.entity.User;

import java.util.Collections;
import java.util.stream.Collectors;

import static ru.sber.shareit.util.mapper.ItemMapper.itemToDto;

public class ItemRequestMapper {
	public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
		return new ItemRequestDto(
				itemRequest.getId(),
				itemRequest.getDescription(),
				itemRequest.getCreated(),
				(itemRequest.getItems() == null) ?
						Collections.emptyList() :
						itemRequest.getItems().stream()
								.map(item -> itemToDto(item, null, null, null))
								.collect(Collectors.toList())
		);
	}

	public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
		return new ItemRequest(
				itemRequestDto.getId(),
				itemRequestDto.getDescription(),
				user,
				itemRequestDto.getCreated(),
				null
		);
	}
}
