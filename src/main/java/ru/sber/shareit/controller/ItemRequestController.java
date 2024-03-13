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
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.security.UserDetailsImpl;
import ru.sber.shareit.service.ItemRequestService;
import ru.sber.shareit.util.ItemRequestValidator;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
	private final ItemRequestService itemRequestService;
	private final ItemRequestValidator itemRequestValidator;

	@PostMapping
	public String create(@ModelAttribute("request") ItemRequestDto requestDto, BindingResult bindingResult) {
		Long userId = getUserId();
		itemRequestValidator.validate(requestDto, bindingResult);
		if (bindingResult.hasErrors()) {
			return "redirect:/items/top"; //??
		}
		itemRequestService.create(userId, requestDto);
		return "items/get-items-by-text"; // поменять
	}

	@GetMapping
	public String getItemRequestsByUserId(Model model) {
		Long userId = getUserId();
		model.addAttribute("requests", itemRequestService.getItemRequestsByUserId(userId));
		return "requests/get-requests-by-user-id";
	}

	@GetMapping("/{requestId}")
	public String getItemRequestById(@PathVariable Long requestId, Model model) {
		Long userId = getUserId();
		ItemRequestDto request = itemRequestService.getItemRequestById(userId, requestId);
		model.addAttribute("request", request);
		ItemDto itemDto = new ItemDto();
		itemDto.setRequestId(requestId);
		itemDto.setName(request.getDescription());
		model.addAttribute("item", itemDto);
		return "requests/get-request-by-id";
	}

	@GetMapping(path = "/all")
	public String getAllItemRequests(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
	                                 @RequestParam(defaultValue = "10") @Positive int size, Model model) {
		Long userId = getUserId();
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("requests", itemRequestService.getAllItemRequests(userId, from, size));
		return "requests/get-requests-all";
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
