package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.entity.*;

import java.util.List;

public class ItemMapper {
	public static ItemDto itemToDto(Item item, Booking last, Booking next, List<CommentDto> comments) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getAvailable(),
				item.getRequest() == null ? null : item.getRequest().getId(),
				last == null ? null : BookingMapper.toBookingDto(last),
				next == null ? null : BookingMapper.toBookingDto(next),
				comments,
				item.getRelevantTemperatureInterval().toString(),
				item.getCity()
		);
	}

	public static Item itemFromDto(ItemDto itemDto, User user, ItemRequest itemRequest) {
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
