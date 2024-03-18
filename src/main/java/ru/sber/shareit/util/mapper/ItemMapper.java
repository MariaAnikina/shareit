package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.entity.*;
import ru.sber.shareit.entity.enums.TemperatureIntervals;

import java.util.List;

public class ItemMapper {
	public static ItemDto toItemDto(Item item, Booking last, Booking next, List<CommentDto> comments) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getAvailable(),
				item.getOwner().getId(),
				item.getRequest() == null ? null : item.getRequest().getId(),
				last == null ? null : BookingMapper.toBookingDto(last),
				next == null ? null : BookingMapper.toBookingDto(next),
				comments,
				item.getRelevantTemperatureInterval().toString(),
				item.getCity()
		);
	}

	public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
		return new Item(
				itemDto.getId(),
				itemDto.getName(),
				itemDto.getDescription(),
				itemDto.getAvailable(),
				user,
				itemRequest,
				TemperatureIntervals.valueOf(itemDto.getRelevantTemperatureInterval()),
				itemDto.getCity()
		);
	}
}
