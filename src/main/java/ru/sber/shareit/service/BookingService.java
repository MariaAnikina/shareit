package ru.sber.shareit.service;

import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.booking.BookingDtoFull;

import java.util.List;

public interface BookingService {
	BookingDtoFull create(Long userId, BookingDto bookingDtoDefault);

	BookingDtoFull approveBooking(Long userId, Long bookingId, boolean approved);

	BookingDtoFull getById(Long userId, Long bookingId);

	List<BookingDtoFull> getUserBookings(Long userId, String stateString, int from, int size);

	List<BookingDtoFull> getOwnerBookings(Long userId, String stateString, int from, int size);
}
