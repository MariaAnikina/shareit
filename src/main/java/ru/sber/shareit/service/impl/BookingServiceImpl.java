package ru.sber.shareit.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.booking.BookingDtoFull;
import ru.sber.shareit.entity.*;
import ru.sber.shareit.exception.*;
import ru.sber.shareit.repository.BookingRepository;
import ru.sber.shareit.repository.ItemRepository;
import ru.sber.shareit.repository.UserRepository;
import ru.sber.shareit.service.BookingService;
import ru.sber.shareit.util.mapper.BookingMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.sber.shareit.util.mapper.BookingMapper.toBooking;
import static ru.sber.shareit.util.mapper.BookingMapper.toBookingDtoFull;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	public BookingDtoFull create(Long userId, BookingDto bookingDto) {
		LocalDateTime start = convertStringToDatetime(bookingDto.getStart());
		LocalDateTime end = convertStringToDatetime(bookingDto.getEnd());
		Optional<User> userOptional = userRepository.findById(userId);
		if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
			throw new BookingTimeException("Момент начала и окончания бронирования должны быть в будущем");
		}
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		Long itemId = bookingDto.getItemId();
		Optional<Item> itemOptional = itemRepository.findById(itemId);
		if (itemOptional.isEmpty()) {
			throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
		}
		Item item = itemOptional.get();
		if (item.getOwner().getId().equals(userId)) {
			throw new BookingNotFoundException("Пользователь не может забронировать свою вещь");
		}
		if (!item.getAvailable()) {
			throw new ItemUnavailableException("Вещь с id=" + itemId + " не доступна");
		}
		if (!end.isAfter(start)) {
			throw new BookingTimeException("Момент окончания бронирования должен быть позже начала");
		}
		bookingDto.setBookerId(userId);
		bookingDto.setBookingStatus(BookingStatus.WAITING);
		Booking booking = bookingRepository.save(
				toBooking(bookingDto, userOptional.get(), item)
		);
		log.info("Добавлено бронирование {}", booking);
		return toBookingDtoFull(booking);
	}

	@Override
	public BookingDtoFull approveBooking(Long userId, Long bookingId, boolean approved) {
		Optional<Booking> bookingOptional = bookingRepository.findByIdAndItemOwnerId(bookingId, userId);
		if (bookingOptional.isEmpty())
			throw new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено");
		Booking booking = bookingOptional.get();
		if (!booking.getBookingStatus().equals(BookingStatus.WAITING))
			throw new BookingStatusException("Статус бронирования не является 'WAITING'");
		if (approved) {
			booking.setBookingStatus(BookingStatus.APPROVED);
			log.info("Пользователь с id={} подтвердил бронирование с id={}", userId, bookingId);
		} else {
			booking.setBookingStatus(BookingStatus.REJECTED);
			log.info("Пользователь с id={} отклонил бронирование с id={}", userId, bookingId);
		}
		return toBookingDtoFull(bookingRepository.save(booking));
	}

	@Transactional(readOnly = true)
	@Override
	public BookingDtoFull getById(Long userId, Long bookingId) {
		if (!userRepository.existsById(userId))
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
		if (bookingOptional.isEmpty())
			throw new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено");
		Booking booking = bookingOptional.get();
		if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
			throw new BookingNotFoundException(
					"У пользователя с id=" + userId + " не обнаружено бронирований с id=" + bookingId
			);
		}
		log.info("Пользователь с id={} запросил бронирование с id={}", userId, bookingId);
		return toBookingDtoFull(booking);
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookingDtoFull> getUserBookings(Long userId, String stateString, int from, int size) {
		BookingState state = checkAvailabilityBookingStatusAndUser(userId, stateString);
		log.info("Пользователь с id={} запросил список своих бронирований со статусом {}", userId, state);
		List<Booking> bookings;
		switch (state) {
			case FUTURE:
				bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
						userId,
						LocalDateTime.now(),
						getPageable(from, size)
				);
				break;
			case CURRENT:
				LocalDateTime now = LocalDateTime.now();
				bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
						userId,
						now,
						now,
						getPageable(from, size)
				);
				break;
			case PAST:
				bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
						userId,
						LocalDateTime.now(),
						getPageable(from, size)
				);
				break;
			case WAITING:
				bookings = bookingRepository.findByBookerIdAndBookingStatusOrderByStartDesc(
						userId,
						BookingStatus.WAITING,
						getPageable(from, size)
				);
				break;
			case REJECTED:
				bookings = bookingRepository.findByBookerIdAndBookingStatusOrderByStartDesc(
						userId,
						BookingStatus.REJECTED,
						getPageable(from, size)
				);
				break;
			default:
				bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, getPageable(from, size));
		}
		return bookings.stream()
				.map(BookingMapper::toBookingDtoFull)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookingDtoFull> getOwnerBookings(Long userId, String stateString, int from, int size) {
		BookingState state = checkAvailabilityBookingStatusAndUser(userId, stateString);
		log.info("Пользователь с id={} запросил список бронирований своих вещей со статусом {}", userId, state);
		List<Booking> bookings;
		switch (state) {
			case FUTURE:
				bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
						userId,
						LocalDateTime.now(),
						getPageable(from, size)
				);
				break;
			case CURRENT:
				LocalDateTime now = LocalDateTime.now();
				bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
						userId,
						now,
						now,
						getPageable(from, size)
				);
				break;
			case PAST:
				bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
						userId,
						LocalDateTime.now(),
						getPageable(from, size)
				);
				break;
			case WAITING:
				bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartDesc(
						userId,
						BookingStatus.WAITING,
						getPageable(from, size)
				);
				break;
			case REJECTED:
				bookings = bookingRepository.findByItemOwnerIdAndBookingStatusOrderByStartDesc(
						userId,
						BookingStatus.REJECTED,
						getPageable(from, size)
				);
				break;
			default:
				bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, getPageable(from, size));
		}
		return bookings.stream()
				.map(BookingMapper::toBookingDtoFull)
				.collect(Collectors.toList());	}

	private BookingState checkAvailabilityBookingStatusAndUser(Long userId, String stateString) {
		BookingState state;
		try {
			state = BookingState.valueOf(stateString);
		} catch (IllegalArgumentException e) {
			throw new BookingStateException("Неизвестный статус бронирования: " + stateString);
		}
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		return state;
	}

	public LocalDateTime convertStringToDatetime(String dateTimeString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		try {
			return LocalDateTime.parse(dateTimeString, formatter);
		} catch (DateTimeParseException e) {
			throw new UnableToConvertStringToDatetimeException("Не удалось распарсить строку " + dateTimeString);
		}
	}

	private Pageable getPageable(int from, int size) {
		int page = from / size;
		return PageRequest.of(page, size);
	}
}
