package ru.sber.shareit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sber.shareit.dto.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class ItemRequestOutDto {
	private Long id;
	private String description;
	private LocalDateTime created;
	private List<ItemDto> items;
}
