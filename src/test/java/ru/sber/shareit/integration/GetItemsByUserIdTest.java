package ru.sber.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.dto.user.UserDto;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.entity.enums.Role;
import ru.sber.shareit.entity.enums.TemperatureIntervals;
import ru.sber.shareit.exception.UserNotFoundException;
import ru.sber.shareit.service.ItemService;
import ru.sber.shareit.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class GetItemsByUserIdTest {
	private final EntityManager em;
	private final ItemService itemService;
	private final UserService userService;

	@Test
	public void getItemsByUserIdTest_whenItemsFound_thenReturnListItemsDto() {
		ItemDto itemDto = new ItemDto(
				null,
				"Test name",
				"Test description",
				true,
				null,
				null,
				null,
				null,
				null,
				TemperatureIntervals.NEUTRAL.toString(),
				"Рязань"
		);
		UserDto userDto = new UserDto(null, "user1", "123", "User1",
				"user1@email.com", Role.ROLE_USER.toString(), "Рязань");
		userService.create(userDto);
		TypedQuery<User> query = em.createQuery("select u from User u where u.username = :username", User.class);
		User user = query.setParameter("username", userDto.getUsername()).getSingleResult();
		Long userId = user.getId();
		itemService.create(userId, itemDto);
		List<ItemDto> items = itemService.getItemsByUserId(userId, 0, 5);
		ItemDto itemDtoOutgoing = items.get(0);

		assertThat(items.size(), equalTo(1));
		assertThat(itemDtoOutgoing.getId(), notNullValue());
		assertThat(itemDtoOutgoing.getName(), equalTo(itemDto.getName()));
		assertThat(itemDtoOutgoing.getDescription(), equalTo(itemDto.getDescription()));
		assertThat(itemDtoOutgoing.getAvailable(), equalTo(itemDto.getAvailable()));
		assertThat(itemDtoOutgoing.getRequestId(), nullValue());
		assertThat(itemDtoOutgoing.getLastBooking(), nullValue());
		assertThat(itemDtoOutgoing.getNextBooking(), nullValue());
		assertThat(itemDtoOutgoing.getComments(), equalTo(Collections.emptyList()));
		assertThat(itemDtoOutgoing.getRelevantTemperatureInterval(), equalTo(itemDto.getRelevantTemperatureInterval().toString()));
		assertThat(itemDtoOutgoing.getCity(), equalTo(itemDto.getCity()));
	}

	@Test
	public void getItemsByUserIdTest_whenUserNotFound_thenReturnUserNotFoundException() {
		UserNotFoundException e = Assertions.assertThrows(
				UserNotFoundException.class,
				() -> itemService.getItemsByUserId(1L, 0, 5)
		);

		assertThat(e.getMessage(), equalTo("Пользователь с id=1 не найден"));
	}
}
