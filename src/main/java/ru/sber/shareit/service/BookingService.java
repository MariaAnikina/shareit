package ru.sber.shareit.service;

import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.booking.BookingDtoFull;

import java.util.List;

/**
 * BookingService interface
 */

public interface BookingService {

	/**
	 * add the booking (save to the database and assign the identity)
	 *
	 * @param userId            owner's id
	 * @param bookingDtoDefault the booking to save and register
	 * @return the booking with assigned id
	 */
	BookingDtoFull create(Long userId, BookingDto bookingDtoDefault);

	/**
	 * set APPROVE or REJECTED BookingStatus for the booking. This is only allowed to the owner of the item and
	 * current BookingStatus must be WAITING
	 *
	 * @param userId    user's id
	 * @param bookingId booking's id
	 * @param approved  boolean
	 * @return the booking with updated (APPROVE or REJECTED) BookingStatus field.
	 */
	BookingDtoFull approveBooking(Long userId, Long bookingId, boolean approved);

	/**
	 * get the booking by booker's id. This is only allowed to item's owner or booker.
	 *
	 * @param userId    user's id
	 * @param bookingId booking's id
	 * @return the requested booking
	 */
	BookingDtoFull getById(Long userId, Long bookingId);

	/**
	 * find if exists list of booking by user's id, sorting by start value, starting with the latest
	 * by state (default = ALL)
	 * with paging option: the size and the number of the page is defined by from/size parameters of request
	 *
	 * @param userId      user's id
	 * @param stateString BookingState value
	 * @param from        number of the page
	 * @param size        number of item requests per page
	 * @return list of bookings of the user according to specified criteria, sorting by start in descending order
	 */

	List<BookingDtoFull> getUserBookings(Long userId, String stateString, int from, int size);

	/**
	 * find if exists list of booking by owner's id, sorting by start value, starting with the latest
	 * by state (default = ALL)
	 * with paging option: the size and the number of the page is defined by from/size parameters of request
	 *
	 * @param userId      owner's id
	 * @param stateString BookingState value
	 * @param from        number of the page
	 * @param size        number of bookings per page
	 * @return list of bookings of item's owner according to specified criteria, sorting by start in descending order
	 */

	List<BookingDtoFull> getOwnerBookings(Long userId, String stateString, int from, int size);
}