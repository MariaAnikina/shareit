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
import ru.sber.shareit.controller.BookingController;
import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.booking.BookingDtoFull;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.dto.user.UserInfoDto;
import ru.sber.shareit.entity.enums.BookingStatus;
import ru.sber.shareit.service.BookingService;
import ru.sber.shareit.util.UserIdUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {
	@MockBean
	private BookingService bookingService;
	@MockBean
	private final UserIdUtil userIdUtil;
	private final MockMvc mvc;

	private final BookingDto bookingDto = new BookingDto(
			1L,
			LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(2),
			1L,
			null,
			null
	);

	private final BookingDtoFull bookingDtoFull = new BookingDtoFull(
			1L,
			LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
			LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS),
			new ItemDto(
					1L,
					"Item1",
					"Test item 1",
					true,
					1L,
					null,
					null,
					null,
					new ArrayList<>(),
					"NEUTRAL",
					"Рязань"
			),
			new UserInfoDto(1L, "test1", "User1",
					"test1@email.com", "ROLE_USER", "Рязань"),
			BookingStatus.WAITING
	);

	@SneakyThrows
	@Test
	void performCreateItemTest_whenBookingCreated_thenReturnViewGetItemById() {
		Mockito.
				when(bookingService.create(anyLong(), any()))
				.thenReturn(bookingDtoFull);
		mvc.perform(post("/bookings/create/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.flashAttr("booking", bookingDto)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("redirect:/items/1"))
				.andExpect(status().is(302));
	}

	@SneakyThrows
	@Test
	void getOwnerBookingsTest_whenBookingsFound_thenReturnViewGetBookingsOwner() {
		List<BookingDtoFull> bookings = List.of(bookingDtoFull);
		Mockito
				.when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt()))
				.thenReturn(bookings);

		mvc.perform(get("/bookings/owner")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("bookings/get-bookings-owner"))
				.andExpect(model().attribute("bookings", bookings))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void getBookingByIdTest_whenBookingFound_thenReturnViewGetBookingById() {
		Mockito
				.when(bookingService.getById(anyLong(), anyLong()))
				.thenReturn(bookingDtoFull);

		mvc.perform(get("/bookings/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("bookings/get-booking-by-id"))
				.andExpect(model().attribute("booking", bookingDtoFull))
				.andExpect(status().isOk());
	}

	@SneakyThrows
	@Test
	void approveBookingTest_whenBookingApprove_thenReturnViewGetBookingById() {
		mvc.perform(patch("/bookings/1?approved=true")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("redirect:/bookings/1"))
				.andExpect(status().is(302));
	}

	@SneakyThrows
	@Test
	void getUserBookingsTest_whenBookingsFound_thenReturnViewGetBookingsUser() {
		List<BookingDtoFull> bookings = List.of(bookingDtoFull);
		Mockito
				.when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
				.thenReturn(bookings);

		mvc.perform(get("/bookings")
						.characterEncoding(StandardCharsets.UTF_8)
						.accept(MediaType.TEXT_HTML))
				.andExpect(view().name("bookings/get-bookings-user"))
				.andExpect(model().attribute("bookings", bookings))
				.andExpect(status().isOk());
	}
}