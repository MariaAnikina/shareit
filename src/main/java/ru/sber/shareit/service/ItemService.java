package ru.sber.shareit.service;

import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;

import java.util.List;

public interface ItemService {
	ItemDto create(Long userId, ItemDto itemDto);

	ItemDto getItemById(Long userId, Long itemId);

	List<ItemDto> getItemsByUserId(Long userId, int from, int size);

	List<ItemDto> getItemsInYourCity(Long userId, int from, int size);

	ItemDto update(Long userId, ItemDto itemDto);

	void delete(Long userId, Long itemId);

	List<ItemDto> findItems(String text, int from, int size);

	CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

	List<ItemDto> getItemsByTemperatureIntervalInYourCity(Long userId, int from, int size);
}
