package ru.sber.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.security.UserDetailsImpl;
import ru.sber.shareit.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
	private final BookingService bookingService;

	@PostMapping("/create/{itemId}")
	public String performCreateItem(@Valid @ModelAttribute("booking") BookingDto bookingDto,
	                                BindingResult bindingResult,
	                                @PathVariable Long itemId) {
		Long userId = getUserId();
		bookingService.create(userId, bookingDto);
		return "redirect:/items/" + itemId;
	}

	@GetMapping("/owner")
	public String getOwnerBookings(
			Model model,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		Long userId = getUserId();
		model.addAttribute("state", state);
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("bookings", bookingService.getOwnerBookings(userId, state, from, size));
		return "bookings/get-bookings-owner";
	}

	@GetMapping("/{bookingId}")
	public String getBookingById(@PathVariable Long bookingId, Model model) {
		Long userId = getUserId();
		model.addAttribute("booking", bookingService.getById(userId, bookingId));
		model.addAttribute("userId", userId);
		return "bookings/get-booking-by-id";
	}

	@PatchMapping("/{bookingId}")
	public String approveBooking(
			@PathVariable Long bookingId,
			@RequestParam boolean approved
	) {
		Long userId = getUserId();
		bookingService.approveBooking(userId, bookingId, approved);
		return "redirect:/bookings/" + bookingId;
	}

	@GetMapping
	public String getUserBookings(
			Model model,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		Long userId = getUserId();
		model.addAttribute("state", state);
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("bookings", bookingService.getUserBookings(userId, state, from, size));
		return "bookings/get-bookings-user";
	}

	private Long getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userId = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			userId = userDetails.getUser().getId();
		}
		return userId;
	}
}
