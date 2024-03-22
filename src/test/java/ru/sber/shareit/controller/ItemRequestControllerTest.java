package ru.sber.shareit.controller;

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
import ru.sber.shareit.controller.ItemRequestController;
import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.service.ItemRequestService;
import ru.sber.shareit.util.UserIdUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc(addFilters = false)
class ItemRequestControllerTest {
	@MockBean
	private final ItemRequestService itemRequestService;
	@MockBean
	private final UserIdUtil userIdUtil;
	private final MockMvc mvc;

	private final ItemRequestDto itemRequestDto = new ItemRequestDto(
			1L,
			"item request description",
			LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
			Collections.emptyList()
	);

	@SneakyThrows
	@Test
	void createTest_whenItemRequestCreated_thenReturnViewGetRequestsAll() {
		Mockito.
				when(itemRequestService.create(anyLong(), any()))
				.thenReturn(itemRequestDto);
		mvc.perform(post("/requests")
						.characterEncoding(StandardCharsets.UTF_8)
						.flashAttr("request", itemRequestDto)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("redirect:/requests"))
				.andExpect(status().is(302));
	}

	@SneakyThrows
	@Test
	void getItemRequestsByUserIdTest_whenItemRequestsFound_thenReturnViewGetRequestsByUserId() {
		List<ItemRequestDto> requests = List.of(itemRequestDto);
		Mockito
				.when(itemRequestService.getItemRequestsByUserId(anyLong()))
				.thenReturn(requests);

		mvc.perform(get("/requests")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("requests/get-requests-by-user-id"))
				.andExpect(model().attribute("requests", requests))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getItemRequestByIdTest_whenItemRequestFound_thenReturnViewGetRequestById() {
		Mockito
				.when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
				.thenReturn(itemRequestDto);

		mvc.perform(get("/requests/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("requests/get-request-by-id"))
				.andExpect(model().attribute("request", itemRequestDto))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getAllItemRequestsTest_whenItemRequestsFound_thenReturnViewGetRequestsAll() {
		List<ItemRequestDto> requests = List.of(itemRequestDto);
		Mockito
				.when(itemRequestService.getAllItemRequests(anyLong(), anyBoolean(), anyInt(), anyInt()))
				.thenReturn(requests);

		mvc.perform(get("/requests/all")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("requests/get-requests-all"))
				.andExpect(model().attribute("requests", requests))
				.andExpect(status().isOk());
	}
}