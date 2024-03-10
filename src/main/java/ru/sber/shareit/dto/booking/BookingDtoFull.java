package ru.sber.shareit.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.entity.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class BookingDtoFull {
	private Long id;
	@NotNull(message = "Поле start не должно быть null")
	@Future(message = "Поле start должно содержать дату, которая еще не наступила")
	private LocalDateTime start;
	@NotNull(message = "Поле end не должно быть null")
	@Future(message = "Поле end должно содержать дату, которая еще не наступила")
	private LocalDateTime end;
	private ItemDto item;
	private UserDto booker;
	private BookingStatus bookingStatus;
}
