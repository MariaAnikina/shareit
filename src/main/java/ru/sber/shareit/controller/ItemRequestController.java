package ru.sber.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.service.ItemRequestService;
import ru.sber.shareit.util.UserIdUtil;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
	private final ItemRequestService itemRequestService;
	private final UserIdUtil userIdUtil;

	@PostMapping
	public String create(@Valid @ModelAttribute("request") ItemRequestDto requestDto, BindingResult bindingResult) {
		Long userId = userIdUtil.getUserId();
		if (bindingResult.hasErrors()) {
			return "redirect:/items/top";
		}
		itemRequestService.create(userId, requestDto);
		return "redirect:/requests";
	}

	@GetMapping
	public String getItemRequestsByUserId(Model model) {
		Long userId = userIdUtil.getUserId();
		model.addAttribute("requests", itemRequestService.getItemRequestsByUserId(userId));
		return "requests/get-requests-by-user-id";
	}

	@GetMapping("/{requestId}")
	public String getItemRequestById(@PathVariable Long requestId, Model model) {
		Long userId = userIdUtil.getUserId();
		ItemRequestDto request = itemRequestService.getItemRequestById(userId, requestId);
		model.addAttribute("request", request);
		ItemDto itemDto = new ItemDto();
		itemDto.setRequestId(requestId);
		itemDto.setName(request.getDescription());
		model.addAttribute("item", itemDto);
		return "requests/get-request-by-id";
	}

	@GetMapping(path = "/all")
	public String getAllItemRequests(@RequestParam(required = false) boolean myCity,
	                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
	                                 @RequestParam(defaultValue = "10") @Positive int size, Model model) {
		Long userId = userIdUtil.getUserId();
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("requests", itemRequestService.getAllItemRequests(userId, myCity, from, size));
		return "requests/get-requests-all";
	}
}
