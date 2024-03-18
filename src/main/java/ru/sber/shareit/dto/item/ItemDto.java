package ru.sber.shareit.dto.item;

import lombok.*;
import ru.sber.shareit.dto.booking.BookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDto {
	private Long id;
	@NotBlank(message = "Имя вещи не может быть пустым")
	private String name;
	@NotBlank(message = "Описание вещи не может быть пустым")
	private String description;
	private Boolean available;
	private Long ownerId;
	private Long requestId;
	private BookingDto lastBooking;
	private BookingDto nextBooking;
	private List<CommentDto> comments;
	@NotNull(message = "Укажите промежуток температуры, когда вещь более актуальна")
	private String relevantTemperatureInterval;
	private String city;
}
