package ru.sber.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.entity.ItemRequest;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.entity.enams.Role;
import ru.sber.shareit.exception.ItemRequestNotFoundException;
import ru.sber.shareit.exception.UserNotFoundException;
import ru.sber.shareit.repository.ItemRepository;
import ru.sber.shareit.repository.ItemRequestRepository;
import ru.sber.shareit.repository.UserRepository;
import ru.sber.shareit.service.impl.ItemRequestServiceImpl;

import java.awt.print.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static ru.sber.shareit.util.mapper.ItemRequestMapper.toItemRequest;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
	@Mock
	private ItemRequestRepository itemRequestRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ItemRepository itemRepository;
	@InjectMocks
	private ItemRequestServiceImpl itemRequestService;

	private User user;
	private ItemRequestDto itemRequestDto;

	@BeforeEach
	public void setUp() {
		user = new User(1L, "user1", "123", "User1",
				"user1@email.com", Role.ROLE_USER, "Рязань");
		itemRequestDto = new ItemRequestDto(
				1L,
				"Test description",
				null,
				Collections.emptyList()
		);
	}

	@Test
	public void createTest_whenItemRequestCreated_thenReturnItemRequestDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRequestRepository.save(any(ItemRequest.class)))
				.then(returnsFirstArg());

		ItemRequestDto itemRequestDtoOutgoing = itemRequestService.create(1L, itemRequestDto);

		assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
		assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
		assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));
	}

	@Test
	public void createTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemRequestService.create(1L, itemRequestDto)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void getItemRequestsByUserIdTest_whenRequestFound_thenReturnItemRequestsDto() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong()))
				.thenReturn(
						List.of(toItemRequest(itemRequestDto, user))
				);
		Mockito
				.when(itemRepository.findByRequestId(anyLong()))
				.thenReturn(Collections.emptyList());

		List<ItemRequestDto> itemRequests = itemRequestService.getItemRequestsByUserId(1L);
		ItemRequestDto itemRequestDtoOutgoing = itemRequests.get(0);

		assertThat(itemRequests.size(), equalTo(1));
		assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
		assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
		assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));
	}

	@Test
	public void getItemRequestsByUserIdTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(false);

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemRequestService.getItemRequestsByUserId(1L)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void getAllItemRequestsTest_whenAllRequestsFound_thenReturnItemRequestsDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(), any()))
				.thenReturn(
						List.of(toItemRequest(itemRequestDto, user))
				);
		Mockito
				.when(itemRepository.findByRequestId(anyLong()))
				.thenReturn(Collections.emptyList());

		List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests(1L, false, 0, 5);
		ItemRequestDto itemRequestDtoOutgoing = itemRequests.get(0);

		assertThat(itemRequests.size(), equalTo(1));
		assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
		assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
		assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));

		verify(itemRequestRepository, never()).findByRequestorIdNotAndRequestorCityIsOrderByCreatedDesc(anyLong(),
				any(), any());

	}

	@Test
	public void getAllItemRequestsTest_whenRequestsInCityFound_thenReturnItemRequestsDto() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));
		Mockito
				.when(itemRequestRepository.findByRequestorIdNotAndRequestorCityIsOrderByCreatedDesc(anyLong(),
						anyString(), any()))
				.thenReturn(
						List.of(toItemRequest(itemRequestDto, user))
				);
		Mockito
				.when(itemRepository.findByRequestId(anyLong()))
				.thenReturn(Collections.emptyList());

		List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequests(1L, true, 0, 5);
		ItemRequestDto itemRequestDtoOutgoing = itemRequests.get(0);

		assertThat(itemRequests.size(), equalTo(1));
		assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
		assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
		assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));

		verify(itemRequestRepository, never()).findByRequestorIdNotOrderByCreatedDesc(anyLong(), any());
	}

	@Test
	public void getAllItemRequestsTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemRequestService.getAllItemRequests(1L, false,0, 5)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void getItemRequestByIdTest_whenRequestFound_thenReturnItemRequestDto() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(itemRequestRepository.findById(anyLong()))
				.thenReturn(
						Optional.of(toItemRequest(itemRequestDto, user))
				);
		Mockito
				.when(itemRepository.findByRequestId(anyLong()))
				.thenReturn(Collections.emptyList());

		ItemRequestDto itemRequestDtoOutgoing = itemRequestService.getItemRequestById(1L,1L);

		assertThat(itemRequestDtoOutgoing.getId(), equalTo(itemRequestDto.getId()));
		assertThat(itemRequestDtoOutgoing.getDescription(), equalTo(itemRequestDto.getDescription()));
		assertThat(itemRequestDtoOutgoing.getItems(), equalTo(itemRequestDto.getItems()));
	}

	@Test
	public void getItemRequestByIdTest_whenUserNotFound_thenReturnUserNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(false);

		UserNotFoundException e = assertThrows(
				UserNotFoundException.class,
				() -> itemRequestService.getItemRequestById(1L, 1L)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}

	@Test
	public void getItemRequestByIdTest_whenRequestNotFound_thenReturnItemRequestNotFoundException() {
		Mockito
				.when(userRepository.existsById(anyLong()))
				.thenReturn(true);
		Mockito
				.when(itemRequestRepository.findById(anyLong()))
				.thenReturn(Optional.empty());

		ItemRequestNotFoundException e = assertThrows(
				ItemRequestNotFoundException.class,
				() -> itemRequestService.getItemRequestById(1L,1L)
		);

		assertThat(e.getMessage(), equalTo("Запрос с id=1 не найден"));
	}
}
