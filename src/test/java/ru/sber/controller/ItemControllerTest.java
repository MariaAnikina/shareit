package ru.sber.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.sber.shareit.controller.ItemController;
import ru.sber.shareit.controller.UserController;
import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.entity.Item;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.entity.enums.Role;
import ru.sber.shareit.entity.enums.TemperatureIntervals;
import ru.sber.shareit.service.ItemService;
import ru.sber.shareit.util.UserIdUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest {
	@MockBean
	private final ItemService itemService;
	@MockBean
	private final UserIdUtil userIdUtil;
	private final MockMvc mvc;
	private final User user = new User(1L, "user1", "123", "User1",
			"user1@email.com", Role.ROLE_USER, "Москва");
	private final ItemDto itemDto = new ItemDto(
			1L,
			"Item1",
			"Test item 1",
			true,
			user.getId(),
			null,
			null,
			null,
			new ArrayList<>(),
			"NEUTRAL",
			"Москва"
	);

	@SneakyThrows
	@Test
	void createItemPageTest_whenPageFound_thenReturnViewCreateItem() {
		mvc.perform(get("/items/create")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/create-item"))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void performCreateItemTest_whenItemValid_thenReturnViewCreateItem() {
		mvc.perform(post("/items/create")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/create-item"))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getItemByIdTest_whenItemFound_thenReturnViewGetItemById() {
		Mockito
				.when(itemService.getItemById(anyLong(), anyLong()))
				.thenReturn(itemDto);

		mvc.perform(get("/items/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/get-item-by-id"))
				.andExpect(model().attribute("item", itemDto))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getItemsTopTest_whenItemsFound_thenReturnViewGetItemsTop() {
		List<ItemDto> items = List.of(itemDto);
		Mockito
				.when(itemService.getItemsByTemperatureIntervalInYourCity(anyLong(), anyInt(), anyInt()))
				.thenReturn(items);

		mvc.perform(get("/items/top")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/get-items-top"))
				.andExpect(model().attribute("items", items))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getItemsYourCityTest_whenItemsFound_thenReturnViewGetItems() {
		List<ItemDto> items = List.of(itemDto);
		Mockito
				.when(itemService.getItemsInYourCity(anyLong(), anyInt(), anyInt()))
				.thenReturn(items);

		mvc.perform(get("/items/city")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/get-items"))
				.andExpect(model().attribute("items", items))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void updateItemPageTest_whenPageFound_thenReturnViewUpdateItem() {
		mvc.perform(get("/items/update/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/update-item"))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void updateCreateItemTest_whenItemValid_thenReturnViewItemsTop() {
		mvc.perform(put("/items/update/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("redirect:/items/top"))
				.andExpect(status().is(302));
	}

	@SneakyThrows
	@Test
	void getItemsByUserIdTest_whenItemsFound_thenReturnViewGetItemsOwner() {
		List<ItemDto> items = List.of(itemDto);
		Mockito
				.when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt()))
				.thenReturn(items);

		mvc.perform(get("/items/owner")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/get-items-owner"))
				.andExpect(model().attribute("items", items))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void findItemsTest_whenItemsFound_thenReturnViewGetItemsOwner() {
		List<ItemDto> items = List.of(itemDto);
		Mockito
				.when(itemService.findItems(anyString(), anyInt(), anyInt()))
				.thenReturn(items);

		mvc.perform(get("/items/search?text=т")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/get-items-by-text"))
				.andExpect(model().attribute("items", items))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void addCommentTest_whenCommentValid_thenReturnViewItemById() {
		CommentDto commentDto = new CommentDto(1L,"text", "TEST", "2024-03-19 22-22");
		mvc.perform(post("/items/create")
						.characterEncoding(StandardCharsets.UTF_8)
						.flashAttr("comment", commentDto)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("items/create-item"))
				.andExpect(status().isOk());
	}

}