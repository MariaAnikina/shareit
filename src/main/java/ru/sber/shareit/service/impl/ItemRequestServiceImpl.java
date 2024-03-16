package ru.sber.shareit.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sber.shareit.dto.request.ItemRequestDto;
import ru.sber.shareit.entity.ItemRequest;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.exception.ItemRequestNotFoundException;
import ru.sber.shareit.exception.UserNotFoundException;
import ru.sber.shareit.repository.ItemRepository;
import ru.sber.shareit.repository.ItemRequestRepository;
import ru.sber.shareit.repository.UserRepository;
import ru.sber.shareit.service.ItemRequestService;
import ru.sber.shareit.util.mapper.ItemRequestMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.sber.shareit.util.mapper.ItemRequestMapper.toItemRequest;
import static ru.sber.shareit.util.mapper.ItemRequestMapper.toItemRequestDto;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

	private final ItemRequestRepository itemRequestRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		itemRequestDto.setCreated(LocalDateTime.now());
		ItemRequest itemRequest = itemRequestRepository.save(toItemRequest(itemRequestDto, userOptional.get()));
		log.info("Пользователем с id={} добавлен запрос {}", userId, itemRequest);
		return toItemRequestDto(itemRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestDto> getItemRequestsByUserId(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		log.info("Пользователь с id={} запросил свои запросы", userId);
		return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId).stream()
				.peek(itemRequest -> itemRequest.setItems(itemRepository.findByRequestId(itemRequest.getId())))
				.map(ItemRequestMapper::toItemRequestDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestDto> getAllItemRequests(Long userId, boolean myCity, int from, int size) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		int page = from / size;
		if (!myCity) {
			log.info("Пользователь с id={} запросил запросы других пользователей", userId);
			return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(page, size)).stream()
					.peek(itemRequest -> itemRequest.setItems(itemRepository.findByRequestId(itemRequest.getId())))
					.map(ItemRequestMapper::toItemRequestDto)
					.collect(Collectors.toList());
		} else {
			log.info("Пользователь с id={} запросил запросы других пользователей в своем городе", userId);
			return itemRequestRepository.findByRequestorIdNotAndRequestorCityIsOrderByCreatedDesc(userId,
							userOptional.get().getCity(), PageRequest.of(page, size)).stream()
					.peek(itemRequest -> itemRequest.setItems(itemRepository.findByRequestId(itemRequest.getId())))
					.map(ItemRequestMapper::toItemRequestDto)
					.collect(Collectors.toList());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
		if (itemRequestOptional.isEmpty()) {
			throw new ItemRequestNotFoundException("Запрос с id=" + requestId + " не найден");
		}
		log.info("Пользователь с id={} запросил запрос с id={}", userId, requestId);
		ItemRequest itemRequest = itemRequestOptional.get();
		itemRequest.setItems(itemRepository.findByRequestId(requestId));
		return toItemRequestDto(itemRequest);
	}
}
