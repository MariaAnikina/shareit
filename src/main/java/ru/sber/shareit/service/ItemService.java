package ru.sber.shareit.service;

import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;

import java.util.List;

/**
 * ItemService interface
 */
public interface ItemService {

	/**
	 * to add item's data (save and assign identity)
	 *
	 * @param userId  owner's id
	 * @param itemDto the item to register
	 * @return the item with assigned id
	 */
	ItemDto create(Long userId, ItemDto itemDto);

	/**
	 * get the item by id
	 *
	 * @param userId owner's id
	 * @param itemId item's id
	 * @return the item
	 */
	ItemDto getItemById(Long userId, Long itemId);

	/**
	 * get all items of a specific user
	 * with paging option: size of the page is defined by from/to parameters of request
	 *
	 * @param userId user's id
	 * @param from   number of the page
	 * @param size   number of item requests per page
	 * @return list of items
	 */
	List<ItemDto> getItemsByUserId(Long userId, int from, int size);

	/**
	 * get all available items from user's city
	 * with paging option: size of the page is defined by from/to parameters of request
	 *
	 * @param userId user's id
	 * @param from   number of the page
	 * @param size   number of item requests per page
	 * @return list of items
	 */
	List<ItemDto> getItemsInYourCity(Long userId, int from, int size);

	/**
	 * update item's properties
	 *
	 * @param userId  owner's id
	 * @param itemDto ItemDto object with properties to update
	 * @return updated item
	 */
	ItemDto update(Long userId, ItemDto itemDto);

	/**
	 * delete item by id and user's id. This is only allowed for the item's owner.
	 *
	 * @param userId user's  id
	 * @param itemId item's id
	 */
	void delete(Long userId, Long itemId);

	/**
	 * search all available items, contained substring in name or description
	 * with paging option: size of the page is defined by from/to parameters of request
	 *
	 * @param text substring for search
	 * @param from number of the page
	 * @param size number of item requests per page
	 * @return list of items or empty list
	 */
	List<ItemDto> findItems(String text, int from, int size);

	/**
	 * add comment to a specific item. This is only allowed when then booking is ended.
	 *
	 * @param userId     author's id
	 * @param itemId     item's id
	 * @param commentDto comment
	 * @return the comment
	 */
	CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

	/**
	 * get item's list from user's city according current temperature in the city
	 * with paging option: size of the page is defined by from/to parameters of request
	 *
	 * @param userId user's id
	 * @param from   number of the page
	 * @param size   number of item requests per page
	 * @return list of items
	 */
	List<ItemDto> getItemsByTemperatureIntervalInYourCity(Long userId, int from, int size);
}

