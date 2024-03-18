package ru.sber.shareit.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.sber.shareit.entity.enums.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
	private Long id;
	@NotNull(message = "Поле start не должно быть null")
	@Future(message = "Поле start должно содержать дату, которая еще не наступила")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime start;
	@NotNull(message = "Поле end не должно быть null")
	@Future(message = "Поле end должно содержать дату, которая еще не наступила")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime end;
	private Long itemId;
	private Long bookerId;
	private BookingStatus bookingStatus;
}
