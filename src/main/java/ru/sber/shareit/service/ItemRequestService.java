package ru.sber.shareit.service;

import ru.sber.shareit.dto.request.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
	ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

	List<ItemRequestDto> getItemRequestsByUserId(Long userId);

	List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);

	ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
