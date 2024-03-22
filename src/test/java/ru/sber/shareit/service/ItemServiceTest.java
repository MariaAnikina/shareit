package ru.sber.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.sber.shareit.config.ConfigurationApp;
import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.entity.*;
import ru.sber.shareit.entity.enums.BookingStatus;
import ru.sber.shareit.entity.enums.Role;
import ru.sber.shareit.entity.enums.TemperatureIntervals;
import ru.sber.shareit.exception.*;
import ru.sber.shareit.repository.*;
import ru.sber.shareit.service.impl.ItemServiceImpl;
import ru.sber.shareit.util.mapper.TemperatureMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static ru.sber.shareit.util.mapper.CommentMapper.toCommentDto;
import static ru.sber.shareit.util.mapper.ItemMapper.toItemDto;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private ItemRequestRepository itemRequestRepository;
	@Mock
	private TemperatureMapper temperatureMapper;
	@Mock
	private ConfigurationApp configurationApp;
	@InjectMocks
	private ItemServiceImpl itemService;

	private User user;
	private Item item;
	private Comment comment;
	private Booking booking;
	private ItemRequest itemRequest;

	@BeforeEach
	public void setUp() {
		user = new User(1L, "user1", "123", "User1",
				"user1@email.com", Role.ROLE_USER, "Рязань");

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

		comment = new Comment(1L, "Test comment", item, user,
				LocalDateTime.now().plusMinutes(5));
		booking = new Booking(
				1L,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2),
				item,
				new User(2L, "user2", "123", "User2",
						"user2@email.com", Role.ROLE_USER, "Москва"),
				BookingStatus.APPROVED
		);
		itemRequest = new ItemRequest(
				1L,
				"Test item request",
				new User(3L, "user3", "123", "User3",
						"user3@email.com", Role.ROLE_USER, "Москва"),
				LocalDateTime.now(),
				Collections.emptyList()
		);
		item.setRequest(itemRequest);
	}

	@Test
	public void getItemByIdTest_whenItemFound_thenReturnItemDto() {
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));
		Mockito
				.when(bookingRepository.findFirstByItemIdAndBookingStatusNotAndStartBeforeOrderByStartDesc(
						anyLong(),
						any(BookingStatus.class),
						any(LocalDateTime.class)
				))
				.thenReturn(null);
		Mockito
				.when(bookingRepository.findFirstByItemIdAndBookingStatusNotAndStartAfterOrderByStartAsc(
						anyLong(),
						any(BookingStatus.class),
						any(LocalDateTime.class)
				))
				.thenReturn(booking);
		Mockito
				.when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong()))
				.thenReturn(List.of(comment));

		ItemDto itemDtoOutgoing = itemService.getItemById(1L, 1L);

		assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
		assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(item.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking().getId(), equalTo(booking.getId()));
		assertThat(itemDtoOutgoing.getComments().size(), equalTo(1));
		assertThat(itemDtoOutgoing.getComments().get(0).getId(), equalTo(comment.getId()));
		assertThat(itemDtoOutgoing.getRelevantTemperatureInterval(), equalTo(item.getRelevantTemperatureInterval().toString()));
		assertThat(itemDtoOutgoing.getCity(), equalTo(item.getCity()));
	}

	@Test
	public void getItemByIdTest_whenItemNotFound_thenReturnItemNotFoundException() {
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		ItemNotFoundException e = assertThrows(
				ItemNotFoundException.class,
				() -> itemService.getItemById(1L, 1L)
		);

		assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
	}

	@Test
	public void getItemsByUserIdTest_whenItemsFound_thenReturnListItemsDto() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(itemRepository.findByOwnerId(anyLong(), any()))
				.thenReturn(List.of(item));
		Mockito
				.when(bookingRepository.findFirstByItemIdAndBookingStatusNotAndStartBeforeOrderByStartDesc(
						anyLong(),
						any(BookingStatus.class),
						any(LocalDateTime.class)
				))
				.thenReturn(null);
		Mockito
				.when(bookingRepository.findFirstByItemIdAndBookingStatusNotAndStartAfterOrderByStartAsc(
						anyLong(),
						any(BookingStatus.class),
						any(LocalDateTime.class)
				))
				.thenReturn(booking);
		Mockito
				.when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong()))
				.thenReturn(List.of(comment));

		List<ItemDto> items = itemService.getItemsByUserId(1L, 0, 5);
		ItemDto itemDtoOutgoing = items.get(0);

		assertThat(items.size(), equalTo(1));
		assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
		assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(item.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking().getId(), equalTo(booking.getId()));
		assertThat(itemDtoOutgoing.getComments().size(), equalTo(1));
		assertThat(itemDtoOutgoing.getComments().get(0).getId(), equalTo(comment.getId()));
		assertThat(itemDtoOutgoing.getRelevantTemperatureInterval(), equalTo(item.getRelevantTemperatureInterval().toString()));
		assertThat(itemDtoOutgoing.getCity(), equalTo(item.getCity()));
	}

	@Test
	public void getItemsByUserIdTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(false);

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemService.getItemsByUserId(1L, 0, 5)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void getItemsInYourCityTest_whenItemsFound_thenReturnListItemsDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRepository.findByCityAndAvailableIsTrue(anyString(), any()))
				.thenReturn(List.of(item));
		Mockito
				.when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong()))
				.thenReturn(List.of(comment));

		List<ItemDto> items = itemService.getItemsInYourCity(1L, 0, 5);
		ItemDto itemDtoOutgoing = items.get(0);

		assertThat(items.size(), equalTo(1));
		assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
		assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(item.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
		assertThat(itemDtoOutgoing.getComments().size(), equalTo(1));
		assertThat(itemDtoOutgoing.getComments().get(0).getId(), equalTo(comment.getId()));
		assertThat(itemDtoOutgoing.getRelevantTemperatureInterval(), equalTo(item.getRelevantTemperatureInterval().toString()));
		assertThat(itemDtoOutgoing.getCity(), equalTo(item.getCity()));
	}

	@Test
	public void getItemsInYourCityTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.empty());
		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemService.getItemsInYourCity(1L, 0, 10)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void createTest_whenItemCreated_thenReturnItemDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRequestRepository.findById(anyLong()))
				.thenReturn(Optional.of(itemRequest));
		Mockito
				.when(itemRepository.save(any(Item.class)))
				.then(returnsFirstArg());

		ItemDto itemDtoOutgoing = itemService.create(1L, toItemDto(item, null, null, null));

		assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
		assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(item.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
		assertThat(itemDtoOutgoing.getComments(), nullValue());
		assertThat(itemDtoOutgoing.getRelevantTemperatureInterval(), equalTo(item.getRelevantTemperatureInterval().toString()));
		assertThat(itemDtoOutgoing.getCity(), equalTo(item.getCity()));
	}

	@Test
	public void createTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemService.create(1L, toItemDto(item, null, null, null))
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void createTest_whenItemRequestNotFound_thenReturnItemRequestNotFoundException() {
		ItemDto itemDto = toItemDto(item, null, null, null);
		itemDto.setRequestId(1L);
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));

		Mockito
				.when(itemRequestRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		ItemRequestNotFoundException e = assertThrows(
				ItemRequestNotFoundException.class,
				() -> itemService.create(1L, itemDto)
		);

		assertThat(e.getMessage(), equalTo("Запрос с id=1 не найден"));
	}

	@Test
	public void updateTest_whenItemUpdate_thenReturnItemDto() {
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));
		Mockito
				.when(itemRequestRepository.findById(anyLong()))
				.thenReturn(Optional.of(itemRequest));
		Mockito
				.when(itemRepository.save(any(Item.class)))
				.then(returnsFirstArg());

		ItemDto itemDtoOutgoing = itemService.update(1L, toItemDto(item, null, null, null));

		assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
		assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(item.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
		assertThat(itemDtoOutgoing.getComments(), nullValue());
		assertThat(itemDtoOutgoing.getRelevantTemperatureInterval(), equalTo(item.getRelevantTemperatureInterval().toString()));
		assertThat(itemDtoOutgoing.getCity(), equalTo(item.getCity()));
	}

	@Test
	public void updateTest_whenItemNotFound_thenReturnItemNotFoundException() {
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		ItemNotFoundException e = assertThrows(
				ItemNotFoundException.class,
				() -> itemService.update(1L, toItemDto(item, null, null, null))
		);

		assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
	}

	@Test
	public void updateTest_whenUserNotOwner_thenReturnItemOwnerException() {
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));
		ItemOwnerException e = assertThrows(
				ItemOwnerException.class,
				() -> itemService.update(2L, toItemDto(item, null, null, null))
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=2 не владеет вещью с id=1"));
	}

	@Test
	public void updateTest_whenWhenItemRequestNotFound_thenReturnItemRequestNotFoundException() {
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));
		Mockito
				.when(itemRequestRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		ItemRequestNotFoundException e = assertThrows(
				ItemRequestNotFoundException.class,
				() -> itemService.update(1L, toItemDto(item, null, null, null))
		);

		assertThat(e.getMessage(), equalTo("Запрос с id=1 не найден"));
	}

	@Test
	public void deleteTest_whenItemFound_thenReturnVoid() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));

		itemService.delete(1L, 1L);

		Mockito.verify(itemRepository).deleteById(anyLong());
	}

	@Test
	public void deleteTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(false);

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemService.delete(1L, 1L)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void deleteTest_whenItemNotFound_thenReturnItemNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		ItemNotFoundException e = assertThrows(
				ItemNotFoundException.class,
				() -> itemService.delete(1L, 1L)
		);

		assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
	}

	@Test
	public void deleteTest_whenUserNotOwner_thenReturnItemOwnerException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));

		ItemOwnerException e = assertThrows(
				ItemOwnerException.class,
				() -> itemService.delete(2L, 1L)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=2 не владеет вещью с id=1"));
	}

	@Test
	public void findItemsTest_whenItemsFound_thenReturnListItemsDto() {
		Mockito
				.when(itemRepository.findByText(any(String.class), any()))
				.thenReturn(List.of(item));

		List<ItemDto> items = itemService.findItems("Test", 0, 5);
		ItemDto itemDtoOutgoing = items.get(0);

		assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
		assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(item.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
		assertThat(itemDtoOutgoing.getComments(), nullValue());
		assertThat(itemDtoOutgoing.getRelevantTemperatureInterval(), equalTo(item.getRelevantTemperatureInterval().toString()));
		assertThat(itemDtoOutgoing.getCity(), equalTo(item.getCity()));
		;
	}

	@Test
	public void findItemsTest_whenTextIsBlank_thenReturnEmptyList() {
		List<ItemDto> items = itemService.findItems("", 0, 5);

		assertThat(0, equalTo(items.size()));
	}

	@Test
	public void addCommentTest_whenCommentCreated_thenReturnCommendDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));
		Mockito
				.when(
						bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
								anyLong(),
								anyLong(),
								any(LocalDateTime.class)
						)
				)
				.thenReturn(true);
		Mockito
				.when(commentRepository.save(any(Comment.class)))
				.thenReturn(comment);

		CommentDto commentDtoOutgoing = itemService.addComment(
				1L,
				1L,
				toCommentDto(comment, user.getName())
		);

		assertThat(commentDtoOutgoing.getId(), equalTo(comment.getId()));
		assertThat(commentDtoOutgoing.getText(), equalTo(comment.getText()));
		assertThat(commentDtoOutgoing.getAuthorName(), equalTo(comment.getAuthor().getName()));
		assertThat(
				commentDtoOutgoing.getCreated(),
				equalTo(comment.getCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
		);
	}

	@Test
	public void addCommentTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemService.addComment(1L, 1L, toCommentDto(comment, user.getName()))
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void addCommentTest_whenItemNotFound_thenReturnItemNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		ItemNotFoundException e = assertThrows(
				ItemNotFoundException.class,
				() -> itemService.addComment(1L, 1L, toCommentDto(comment, user.getName()))
		);

		assertThat(e.getMessage(), equalTo("Вещь с id=1 не найдена"));
	}

	@Test
	public void addCommentTest_whenBookingNotEnded_thenReturnBookingEndTimeException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRepository.findById(anyLong()))
				.thenReturn(Optional.of(item));
		Mockito
				.when(
						bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
								anyLong(),
								anyLong(),
								any(LocalDateTime.class)
						)
				)
				.thenReturn(false);

		BookingEndTimeException e = assertThrows(
				BookingEndTimeException.class,
				() -> itemService.addComment(1L, 1L, toCommentDto(comment, user.getName()))
		);

		assertThat(e.getMessage(), equalTo("Бронирование еще не завершилось или вы не владели этой вещью"));
	}

	@Test
	public void getItemsByTemperatureIntervalInYourCityTest_whenItemsFound_thenReturnListItemsDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(configurationApp.getLink()).thenReturn("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s");

		Mockito
				.when(configurationApp.getKey()).thenReturn("0559176db0fdda660a02176fe8a89461");
		Mockito
				.when(temperatureMapper.getTemperatureInCelsiusFromString(anyString()))
				.thenReturn(TemperatureIntervals.NEUTRAL);
		Mockito
				.when(itemRepository.findAllByOwnerIdIsNotAndAvailableIsTrueAndRelevantTemperatureIntervalIsAndCityOrderByName(
						anyLong(),
						any(TemperatureIntervals.class),
						anyString(),
						any())
				)
				.thenReturn(new PageImpl<>(List.of(item)));
		Mockito
				.when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong()))
				.thenReturn(Collections.emptyList());

		List<ItemDto> items = itemService.getItemsByTemperatureIntervalInYourCity(1L, 0, 10);
		ItemDto itemDtoOutgoing = items.get(0);

		assertThat(itemDtoOutgoing.getId(), equalTo(item.getId()));
		assertThat(itemDtoOutgoing.getName(), equalTo(item.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(item.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(item.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), equalTo(item.getRequest().getId()));
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
		assertThat(itemDtoOutgoing.getComments(), equalTo(Collections.emptyList()));
		assertThat(itemDtoOutgoing.getRelevantTemperatureInterval(), equalTo(item.getRelevantTemperatureInterval().toString()));
		assertThat(itemDtoOutgoing.getCity(), equalTo(item.getCity()));
		;

	}
}