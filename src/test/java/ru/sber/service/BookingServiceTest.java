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
import ru.sber.shareit.entity.enams.BookingStatus;
import ru.sber.shareit.entity.enams.Role;
import ru.sber.shareit.entity.enams.TemperatureIntervals;
import ru.sber.shareit.exception.UserNotFoundException;
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
}
