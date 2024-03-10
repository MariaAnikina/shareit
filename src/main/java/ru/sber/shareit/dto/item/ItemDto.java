package ru.sber.shareit.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.entity.TemperatureIntervals;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
	private Long id;
	@NotBlank(message = "Имя вещи не может быть пустым")
	private String name;
	@NotBlank(message = "Описание вещи не может быть пустым")
	private String description;
	@NotNull(message = "Должна быть указана доступность вещи")
	private Boolean available;
	private Long requestId;
	private BookingDto lastBooking;
	private BookingDto nextBooking;
	private List<CommentDto> comments;
	@NotNull(message = "Укажите промежуток температуры, когда вещь более актуальна")
	private String relevantTemperatureInterval;
	private String city;
}
