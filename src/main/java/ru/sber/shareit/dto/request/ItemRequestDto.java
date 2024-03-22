package ru.sber.shareit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sber.shareit.dto.item.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDto {
	private Long id;
	@NotNull
	@NotBlank(message = "Описание запроса не может быть пустым")
	private String description;
	private LocalDateTime created;
	private List<ItemDto> items;
}
