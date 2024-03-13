package ru.sber.shareit.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sber.shareit.entity.enams.BookingStatus;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
	private Long id;
	@NotNull(message = "Поле start не должно быть null")
	private String start;
	@NotNull(message = "Поле end не должно быть null")
	private String end;
	private Long itemId;
	private Long bookerId;
	private BookingStatus bookingStatus;
}
