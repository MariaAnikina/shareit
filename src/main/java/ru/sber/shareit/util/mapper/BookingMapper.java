package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.booking.BookingDtoFull;
import ru.sber.shareit.entity.Booking;
import ru.sber.shareit.entity.Item;
import ru.sber.shareit.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingMapper {
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {
		return new Booking(
				bookingDto.getId(),
				LocalDateTime.parse(bookingDto.getStart(), formatter),
				LocalDateTime.parse(bookingDto.getEnd(), formatter),
				item,
				booker,
				bookingDto.getBookingStatus()
		);
	}

	public static BookingDtoFull toBookingDtoFull(Booking booking) {
		return new BookingDtoFull(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				ItemMapper.itemToDto(booking.getItem(), null, null, null),
				UserMapper.toUserDto(booking.getBooker()),
				booking.getBookingStatus()
		);
	}

	public static BookingDto toBookingDto(Booking booking) {
		return new BookingDto(
				booking.getId(),
				booking.getStart().toString(),
				booking.getEnd().toString(),
				booking.getItem().getId(),
				booking.getBooker().getId(),
				booking.getBookingStatus()
		);
	}
}