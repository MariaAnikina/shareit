package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.booking.BookingDtoFull;
import ru.sber.shareit.entity.Booking;
import ru.sber.shareit.entity.Item;
import ru.sber.shareit.entity.User;

import java.time.format.DateTimeFormatter;

public class BookingMapper {
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {
		return new Booking(
				bookingDto.getId(),
				bookingDto.getStart(),
				bookingDto.getEnd(),
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
				ItemMapper.toItemDto(booking.getItem(), null, null, null),
				UserMapper.toUserDto(booking.getBooker()),
				booking.getBookingStatus()
		);
	}

	public static BookingDto toBookingDto(Booking booking) {
		return new BookingDto(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				booking.getItem().getId(),
				booking.getBooker().getId(),
				booking.getBookingStatus()
		);
	}
}