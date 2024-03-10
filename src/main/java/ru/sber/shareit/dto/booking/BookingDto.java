package ru.sber.shareit.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.sber.shareit.entity.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDto {
	private Long id;
	@NotNull(message = "Поле start не должно быть null")
	@Future(message = "Поле start должно содержать дату, которая еще не наступила")
	private final LocalDateTime start;
	@NotNull(message = "Поле end не должно быть null")
	@Future(message = "Поле end должно содержать дату, которая еще не наступила")
	private final LocalDateTime end;
	@NotNull(message = "Id бронируемой вещи не должно быть null")
	private final Long itemId;
	private Long bookerId;
	private BookingStatus bookingStatus;
}
