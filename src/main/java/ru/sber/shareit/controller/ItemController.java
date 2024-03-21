package ru.sber.shareit.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sber.shareit.dto.booking.BookingDto;
import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.service.ItemService;
import ru.sber.shareit.util.UserIdUtil;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Controller
@RequestMapping("/items")
public class ItemController {
	private final ItemService itemService;
	private final UserIdUtil userIdUtil;

	@GetMapping("/create")
	public String createItemPage(@ModelAttribute("item") ItemDto itemDto) {
		return "items/create-item";
	}

	@PostMapping("/create")
	public String performCreateItem(@Valid @ModelAttribute("item") ItemDto itemDto,
	                                BindingResult bindingResult) {
		Long userId = userIdUtil.getUserId();
		if (bindingResult.hasErrors()) {
			return "items/create-item";
		}
		itemService.create(userId, itemDto);
		return "redirect:/items/owner";
	}

	@GetMapping("/{itemId}")
	public String getItemById(@PathVariable Long itemId, Model model) {
		Long userId = userIdUtil.getUserId();
		ItemDto itemById = itemService.getItemById(userId, itemId);
		model.addAttribute("userId", userId);
		model.addAttribute("item", itemById);
		model.addAttribute("itemId", itemId);
		model.addAttribute("comment", new CommentDto());
		model.addAttribute("booking", new BookingDto());
		model.addAttribute("format", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		return "items/get-item-by-id";
	}

	@GetMapping("/top")
	public String getItemsTop(Model model, @RequestParam(defaultValue = "0") @PositiveOrZero int from,
	                          @RequestParam(defaultValue = "3") @Positive int size) {
		Long userId = userIdUtil.getUserId();
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("items", itemService.getItemsByTemperatureIntervalInYourCity(userId, from, size));
		return "items/get-items-top";
	}

	@GetMapping("/city")
	public String getItemsYourCity(Model model, @RequestParam(defaultValue = "0") @PositiveOrZero int from,
	                               @RequestParam(defaultValue = "3") @Positive int size) {
		Long userId = userIdUtil.getUserId();
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("userId", userId);
		model.addAttribute("items", itemService.getItemsInYourCity(userId, from, size));
		return "items/get-items";
	}

	@GetMapping("/update/{itemId}")
	public String updateItemPage(@PathVariable Long itemId, Model model) {
		model.addAttribute("itemId", itemId);
		model.addAttribute("item", new ItemDto());
		return "items/update-item";
	}

	@PutMapping("/update/{itemId}")
	public String performUpdateUser(@ModelAttribute("item") ItemDto itemDto,
	                                BindingResult bindingResult, @PathVariable Long itemId) {
		Long userId = userIdUtil.getUserId();
		itemDto.setId(itemId);
		itemService.update(userId, itemDto);
		return "redirect:/items/top";
	}

	@DeleteMapping("/delete/{itemId}")
	public String deleteUser(@PathVariable Long itemId) {
		Long userId = userIdUtil.getUserId();
		itemService.delete(userId, itemId);
		return "redirect:/items/top";
	}

	@GetMapping("/owner")
	public String getItemsByUserId(Model model, @RequestParam(defaultValue = "0") @PositiveOrZero int from,
	                               @RequestParam(defaultValue = "10") @Positive int size) {
		Long userId = userIdUtil.getUserId();
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("items", itemService.getItemsByUserId(userId, from, size));
		return "items/get-items-owner";
	}

	@GetMapping("/search")
	public String findItems(Model model, @RequestParam String text,
	                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
	                        @RequestParam(defaultValue = "3") @Positive int size) {
		model.addAttribute("text", text);
		model.addAttribute("currentPage", from / size);
		model.addAttribute("size", size);
		model.addAttribute("items", itemService.findItems(text, from, size));
		model.addAttribute("request", new ItemRequestDto());
		return "items/get-items-by-text";
	}

	@PostMapping("/comment/{itemId}")
	public String addComment(@Valid @ModelAttribute("comment") CommentDto commentDto, @PathVariable Long itemId) {
		Long userId = userIdUtil.getUserId();
		itemService.addComment(userId, itemId, commentDto);
		return "redirect:/items/" + itemId;
	}
}
