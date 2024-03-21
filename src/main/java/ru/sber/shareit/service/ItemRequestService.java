package ru.sber.shareit.service;

import ru.sber.shareit.dto.request.ItemRequestDto;

import java.util.List;

/**
 * ItemRequestService interface
 */

public interface ItemRequestService {
	/**
	 * to add item's request (save and assign identity)
	 *
	 * @param userId         owner's id
	 * @param itemRequestDto item's request to save and register
	 * @return item's request with assigned id
	 */
	ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

	/**
	 * to get list of the items' requests and answers to them from a specific user
	 *
	 * @param userId requester id
	 * @return list of the item's requests with answers to them
	 */
	List<ItemRequestDto> getItemRequestsByUserId(Long userId);

	/**
	 * to get list of the other users' item's requests to answer
	 * in case myCity is true it should return item requests only from user's city
	 * list should be started with the latest requests
	 * with paging option: size of the page is defined by from/to parameters of request
	 *
	 * @param userId requester id
	 * @param from   number of the page
	 * @param size   number of item requests per page
	 * @param myCity boolean
	 * @return list of the other users' item's requests
	 */
	List<ItemRequestDto> getAllItemRequests(Long userId, boolean myCity, int from, int size);

	/**
	 * to get item's request by id
	 *
	 * @param userId    user's id
	 * @param requestId item's request id
	 * @return item's request
	 */
	ItemRequestDto getItemRequestById(Long userId, Long requestId);
}