package ru.sber.shareit.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.sber.shareit.dto.booking.BookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {
	private Long id;
	@NotBlank(message = "Имя вещи не может быть пустым")
	private String name;
	@NotBlank(message = "Описание вещи не может быть пустым")
	private String description;
	@JsonProperty("available")
	@NotNull(message = "Должна быть указана доступность вещи")
	private final Boolean available;
	private Long requestId;
	private final BookingDto lastBooking;
	private final BookingDto nextBooking;
	private final List<CommentDto> comments;
}
