package ru.sber.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.booking.BookingDtoFull;
import ru.sber.shareit.entity.Booking;
import ru.sber.shareit.entity.Item;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.entity.enums.BookingStatus;
import ru.sber.shareit.entity.enums.Role;
import ru.sber.shareit.entity.enums.TemperatureIntervals;
import ru.sber.shareit.exception.*;
import ru.sber.shareit.repository.BookingRepository;
import ru.sber.shareit.repository.ItemRepository;
import ru.sber.shareit.repository.UserRepository;
import ru.sber.shareit.service.impl.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static ru.sber.shareit.util.mapper.BookingMapper.toBooking;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ItemRepository itemRepository;
	@InjectMocks
	private BookingServiceImpl bookingService;

	private User user;
	private Item item;
	private BookingDto bookingDto;

	@BeforeEach
	public void setUp() {
		user = new User(1L, "user1", "123", "User1",
				"user1@email.com", Role.ROLE_USER, "Рязань");

		item = new Item(
				1L,
				"Item1",
				"Test item 1",
				true,
				new User(2L, "user2", "123", "User2",
						"user2@email.com", Role.ROLE_USER, "Москва"),
				null,
				TemperatureIntervals.NEUTRAL,
				"Мытищи"
		);

		bookingDto = new BookingDto(
				1L,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2),
				1L,
				1L,
				BookingStatus.WAITING
		);
	}

	@Test
	public void createTest_whenBookingCreated_thenReturnBookingDtoFull() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));
		Mockito
				.when(bookingRepository.save(any(Booking.class)))
				.then(returnsFirstArg());

		BookingDtoFull bookingDtoOutgoing = bookingService.create(1L, bookingDto);

		assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoOutgoing.getBookingStatus(), equalTo(bookingDto.getBookingStatus()));
	}

	@Test
	public void createTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> bookingService.create(1L, bookingDto)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}
	@Test
	public void createTest_whenItemNotFound_thenReturnItemNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		ItemNotFoundException e = assertThrows(
				ItemNotFoundException.class,
				() -> bookingService.create(1L, bookingDto)
		);

		assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
	}

	@Test
	public void createTest_whenUserIsOwner_thenReturnBookingNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));

		item = new Item(
				1L,
				"Item1",
				"Test item 1",
				true,
				user,
				null,
				TemperatureIntervals.NEUTRAL,
				"Москва"
		);
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));

		BookingNotFoundException e = Assertions.assertThrows(
				BookingNotFoundException.class,
				() -> bookingService.create(user.getId(), bookingDto)
		);

		assertThat(e.getMessage(), equalTo("Пользователь не может забронировать свою вещь"));
	}

	@Test
	public void createTest_whenItemNotAvailable_thenReturnItemUnavailableException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));

		item = new Item(
				1L,
				"Item1",
				"Test item 1",
				false,
				new User(2L, "user2", "123", "User2",
						"user2@email.com", Role.ROLE_USER, "Москва"),
				null,
				TemperatureIntervals.NEUTRAL,
				"Москва"
		);
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));

		ItemUnavailableException e = Assertions.assertThrows(
				ItemUnavailableException.class,
				() -> bookingService.create(user.getId(), bookingDto)
		);

		assertThat(e.getMessage(), equalTo("Вещь с id=1 не доступна"));
	}

	@Test
	public void createTest_whenBookingTimeNotValid_thenReturnBookingTimeException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));
		bookingDto = new BookingDto(
				1L,
				LocalDateTime.now().plusDays(2),
				LocalDateTime.now().plusDays(1),
				1L,
				1L,
				BookingStatus.WAITING
		);

		BookingTimeException e = Assertions.assertThrows(
				BookingTimeException.class,
				() -> bookingService.create(user.getId(), bookingDto)
		);

		assertThat(e.getMessage(), equalTo("Момент окончания бронирования должен быть позже начала"));
	}

	@Test
	public void approveBookingTest_whenBookingApprove_thenReturnBookingDtoFull() {
		Mockito
				.when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
				.thenReturn(Optional.of(
						toBooking(bookingDto, user, item)
				));
		Mockito
				.when(bookingRepository.save(any(Booking.class)))
				.then(returnsFirstArg());

		BookingDtoFull bookingDtoOutgoing = bookingService.approveBooking(2L, 1L, true);

		assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoOutgoing.getBookingStatus(), equalTo(BookingStatus.APPROVED));
	}

	@Test
	public void approveBookingTest_whenBookingNotApprove_thenReturnBookingDtoFull() {
		Mockito
				.when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
				.thenReturn(Optional.of(
						toBooking(bookingDto, user, item)
				));
		Mockito
				.when(bookingRepository.save(any(Booking.class)))
				.then(returnsFirstArg());

		BookingDtoFull bookingDtoOutgoing = bookingService.approveBooking(2L, 1L, false);

		assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoOutgoing.getBookingStatus(), equalTo(BookingStatus.REJECTED));
	}

	@Test
	public void approveBookingTest_whenBookingNotFound_thenReturnBookingNotFoundException() {
		Mockito
				.when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
				.thenReturn(Optional.empty());

		BookingNotFoundException e = Assertions.assertThrows(
				BookingNotFoundException.class,
				() -> bookingService.approveBooking(1L, 1L, true)
		);

		assertThat(e.getMessage(), equalTo("Бронирование с id=1 не найдено"));
	}

	@Test
	public void approveBookingTest_whenBookingStatusNotWAITING_thenReturnBookingStatusException() {
		bookingDto = new BookingDto(
				1L,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2),
				1L,
				1L,
				BookingStatus.CANCELED
		);
		Mockito
				.when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
				.thenReturn(Optional.of(
						toBooking(bookingDto, user, item)
				));

		BookingStatusException e = Assertions.assertThrows(
				BookingStatusException.class,
				() -> bookingService.approveBooking(2L, 1L, true)
		);

		assertThat(e.getMessage(), equalTo("Статус бронирования не является 'WAITING'"));
	}

	@Test
	public void getByIdTest_whenBookingFound_thenReturnBookingDtoFull() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(bookingRepository.findById(anyLong()))
				.thenReturn(Optional.of(
						toBooking(bookingDto, user, item)
				));

		BookingDtoFull bookingDtoOutgoing = bookingService.getById(1L, 1L);

		assertThat(bookingDtoOutgoing.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoOutgoing.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoOutgoing.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoOutgoing.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoOutgoing.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoOutgoing.getBookingStatus(), equalTo(bookingDto.getBookingStatus()));
	}

	@Test
	public void getByIdTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(false);

		UserNotFoundException e = Assertions.assertThrows(
				UserNotFoundException.class,
				() -> bookingService.getById(1L, 1L)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void getByIdTest_whenBookingNotFound_thenReturnBookingNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(bookingRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		BookingNotFoundException e = Assertions.assertThrows(
				BookingNotFoundException.class,
				() -> bookingService.getById(1L, 1L)
		);

		assertThat(e.getMessage(), equalTo("Бронирование с id=1 не найдено"));
	}

	@Test
	public void getByIdTest_whenUserBookingNotFound_thenReturnBookingNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(bookingRepository.findById(anyLong()))
				.thenReturn(Optional.of(
						toBooking(bookingDto, user, item)
				));

		BookingNotFoundException e = Assertions.assertThrows(
				BookingNotFoundException.class,
				() -> bookingService.getById(3L, 1L)
		);

		assertThat(e.getMessage(), equalTo("У пользователя с id=3 не обнаружено бронирований с id=1"));
	}

	@Test
	public void getUserBookingsTest_whenBookingStateALL_thenReturnListBookingsDtoFull() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(
						bookingRepository.findByBookerIdOrderByStartDesc(
								anyLong(),
								any(Pageable.class))
				)
				.thenReturn(List.of(
						toBooking(bookingDto, user, item)
				));

		List<BookingDtoFull> bookings = bookingService.getUserBookings(1L, "ALL", 0, 5);
		BookingDtoFull bookingDtoFull = bookings.get(0);

		assertThat(bookings.size(), equalTo(1));
		assertThat(bookingDtoFull.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoFull.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoFull.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoFull.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoFull.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoFull.getBookingStatus(), equalTo(bookingDto.getBookingStatus()));
	}

	@Test
	public void getUserBookingsTest_whenBookingStateWAITING_thenReturnListBookingsDtoFull() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(
						bookingRepository.findByBookerIdAndBookingStatusOrderByStartDesc(
								anyLong(),
								any(BookingStatus.class),
								any(Pageable.class))
				)
				.thenReturn(List.of(
						toBooking(bookingDto, user, item)
				));

		List<BookingDtoFull> bookings = bookingService.getUserBookings(1L, "WAITING", 0, 5);
		BookingDtoFull bookingDtoFull = bookings.get(0);

		assertThat(bookings.size(), equalTo(1));
		assertThat(bookingDtoFull.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoFull.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoFull.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoFull.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoFull.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoFull.getBookingStatus(), equalTo(bookingDto.getBookingStatus()));
	}

	@Test
	public void getUserBookingsTest_whenBookingStateFUTURE_thenReturnListBookingsDtoFull() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(
						bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
								anyLong(),
								any(LocalDateTime.class),
								any(Pageable.class))
				)
				.thenReturn(List.of(
						toBooking(bookingDto, user, item)
				));

		List<BookingDtoFull> bookings = bookingService.getUserBookings(1L, "FUTURE", 0, 5);
		BookingDtoFull bookingDtoFull = bookings.get(0);

		assertThat(bookings.size(), equalTo(1));
		assertThat(bookingDtoFull.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoFull.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoFull.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoFull.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoFull.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoFull.getBookingStatus(), equalTo(bookingDto.getBookingStatus()));
	}

	@Test
	public void getUserBookingsTest_whenBookingStateNotFound_thenReturnBookingStateException() {
		BookingStateException e = Assertions.assertThrows(
				BookingStateException.class,
				() -> bookingService.getUserBookings(1L, "Test", 0, 5)
		);

		assertThat(e.getMessage(), equalTo("Неизвестный статус бронирования: Test"));
	}

	@Test
	public void getUserBookingsTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(false);

		UserNotFoundException e = Assertions.assertThrows(
				UserNotFoundException.class,
				() -> bookingService.getUserBookings(1L, "ALL", 0, 5)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void getOwnerBookingsTest_whenBookingStateALL_thenReturnListBookingsDtoFull() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(
						bookingRepository.findByItemOwnerIdOrderByStartDesc(
								anyLong(),
								any(Pageable.class))
				)
				.thenReturn(List.of(
						toBooking(bookingDto, user, item)
				));

		List<BookingDtoFull> bookings = bookingService.getOwnerBookings(2L, "ALL", 0, 5);
		BookingDtoFull bookingDtoFull = bookings.get(0);

		assertThat(bookings.size(), equalTo(1));
		assertThat(bookingDtoFull.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoFull.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoFull.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoFull.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoFull.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoFull.getBookingStatus(), equalTo(bookingDto.getBookingStatus()));
	}

	@Test
	public void getOwnerBookingsTest_whenBookingStateWAITING_thenReturnListBookingsDtoFull() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(
						bookingRepository.findByItemOwnerIdOrderByStartDesc(
								anyLong(),
								any(Pageable.class))
				)
				.thenReturn(List.of(
						toBooking(bookingDto, user, item)
				));

		List<BookingDtoFull> bookings = bookingService.getOwnerBookings(2L, "ALL", 0, 5);
		BookingDtoFull bookingDtoFull = bookings.get(0);

		assertThat(bookings.size(), equalTo(1));
		assertThat(bookingDtoFull.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoFull.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoFull.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoFull.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoFull.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoFull.getBookingStatus(), equalTo(bookingDto.getBookingStatus()));
	}

	@Test
	public void getOwnerBookingsTest_whenBookingStateFUTURE_thenReturnListBookingsDtoFull() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(
						bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
								anyLong(),
								any(LocalDateTime.class),
								any(Pageable.class))
				)
				.thenReturn(List.of(
						toBooking(bookingDto, user, item)
				));

		List<BookingDtoFull> bookings = bookingService.getOwnerBookings(2L, "FUTURE", 0, 5);
		BookingDtoFull bookingDtoFull = bookings.get(0);

		assertThat(bookings.size(), equalTo(1));
		assertThat(bookingDtoFull.getId(), equalTo(bookingDto.getId()));
		assertThat(bookingDtoFull.getStart(), equalTo(bookingDto.getStart()));
		assertThat(bookingDtoFull.getEnd(), equalTo(bookingDto.getEnd()));
		assertThat(bookingDtoFull.getItem().getId(), equalTo(bookingDto.getItemId()));
		assertThat(bookingDtoFull.getBooker().getId(), equalTo(bookingDto.getBookerId()));
		assertThat(bookingDtoFull.getBookingStatus(), equalTo(bookingDto.getBookingStatus()));
	}

	@Test
	public void getOwnerBookingsTest_whenBookingStateNotFound_thenReturnBookingStateException() {
		BookingStateException e = Assertions.assertThrows(
				BookingStateException.class,
				() -> bookingService.getOwnerBookings(1L, "Test", 0, 5)
		);

		assertThat(e.getMessage(), equalTo("Неизвестный статус бронирования: Test"));
	}

	@Test
	public void getOwnerBookingsTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(false);

		UserNotFoundException e = Assertions.assertThrows(
				UserNotFoundException.class,
				() -> bookingService.getOwnerBookings(1L, "ALL", 0, 5)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}
}
