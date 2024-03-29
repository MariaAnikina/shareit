package ru.sber.shareit.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.sber.shareit.config.ConfigurationApp;
import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.dto.item.ItemDto;
import ru.sber.shareit.entity.*;
import ru.sber.shareit.entity.enums.BookingStatus;
import ru.sber.shareit.entity.enums.TemperatureIntervals;
import ru.sber.shareit.exception.*;
import ru.sber.shareit.repository.*;
import ru.sber.shareit.service.ItemService;
import ru.sber.shareit.util.mapper.TemperatureMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.sber.shareit.util.mapper.CommentMapper.toComment;
import static ru.sber.shareit.util.mapper.CommentMapper.toCommentDto;
import static ru.sber.shareit.util.mapper.ItemMapper.toItem;
import static ru.sber.shareit.util.mapper.ItemMapper.toItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;
	private final TemperatureMapper temperatureMapper;
	private final ItemRequestRepository itemRequestRepository;
	private final ConfigurationApp configuration;

	@Override
	@Transactional
	public ItemDto create(Long userId, ItemDto itemDto) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		itemDto.setAvailable(true);
		ItemRequest itemRequest = null;
		Long requestId = itemDto.getRequestId();
		if (requestId != null) {
			Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
			if (itemRequestOptional.isEmpty()) {
				throw new ItemRequestNotFoundException("Запрос с id=" + requestId + " не найден");
			}
			itemRequest = itemRequestOptional.get();
		}
		User user = userOptional.get();
		if (itemDto.getCity() == null) {
			itemDto.setCity(user.getCity());
		}
		Item item = itemRepository.save(
				toItem(itemDto, user, itemRequest)
		);
		log.info("Добавлена вещь {}", item);
		return toItemDto(item, null, null, null);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemDto getItemById(Long userId, Long itemId) {
		Optional<Item> itemOptional = itemRepository.findById(itemId);
		if (itemOptional.isEmpty()) {
			throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
		}
		Item item = itemOptional.get();
		LocalDateTime now = LocalDateTime.now();
		Booking last = bookingRepository.findFirstByItemIdAndBookingStatusNotAndStartBeforeOrderByStartDesc(
				itemId,
				BookingStatus.REJECTED,
				now
		);
		Booking next = bookingRepository.findFirstByItemIdAndBookingStatusNotAndStartAfterOrderByStartAsc(
				itemId,
				BookingStatus.REJECTED,
				now
		);
		List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
				.map(comment -> toCommentDto(comment, comment.getAuthor().getName()))
				.collect(Collectors.toList());
		log.info("Запрошена вещь {}", item);
		return toItemDto(item, last, next, comments);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemDto> getItemsByUserId(Long userId, int from, int size) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		log.info("Запрошен список вещей пользователя с id={}", userId);
		return itemRepository.findByOwnerId(userId, getPageable(from, size)).stream()
				.map(this::ItemToItemDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public List<ItemDto> getItemsInYourCity(Long userId, int from, int size) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		log.info("Запрошен список вещей пользователем с id={}", userId);
		return itemRepository.findByCityAndAvailableIsTrue(userOptional.get().getCity(), getPageable(from, size)).stream()
				.map(this::toItemDtoInfo)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public ItemDto update(Long userId, ItemDto itemDto) {
		Long itemId = itemDto.getId();
		Optional<Item> itemOptional = itemRepository.findById(itemId);
		if (itemOptional.isEmpty()) {
			throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
		}
		Item oldItem = itemOptional.get();
		User owner = oldItem.getOwner();
		if (!owner.getId().equals(userId)) {
			throw new ItemOwnerException("Пользователь с id=" + userId + " не владеет вещью с id=" + itemId);
		}
		ItemRequest itemRequest = null;
		Long requestId = itemDto.getRequestId();
		if (requestId != null) {
			Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(requestId);
			if (itemRequestOptional.isEmpty()) {
				throw new ItemRequestNotFoundException("Запрос с id=" + requestId + " не найден");
			}
			itemRequest = itemRequestOptional.get();
		}
		Item updateItem = toItem(itemDto, owner, itemRequest);

		String name = updateItem.getName();
		String description = updateItem.getDescription();

		if (name == null || name.isBlank()) {
			updateItem.setName(oldItem.getName());
		}
		if (description == null || description.isBlank()) {
			updateItem.setDescription(oldItem.getDescription());
		}
		if (updateItem.getRelevantTemperatureInterval() == null) {
			updateItem.setDescription(oldItem.getDescription());
		}
		if (updateItem.getAvailable() == null) {
			updateItem.setAvailable(oldItem.getAvailable());
		}
		updateItem.setCity(oldItem.getCity());
		Item item = itemRepository.save(updateItem);
		log.info("Обновлена вещь {}", item);
		return toItemDto(item, null, null, null);
	}

	@Override
	@Transactional
	public void delete(Long userId, Long itemId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		Optional<Item> itemOptional = itemRepository.findById(itemId);
		if (itemOptional.isEmpty()) {
			throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
		}
		if (!userId.equals(itemOptional.get().getOwner().getId())) {
			throw new ItemOwnerException("Пользователь с id=" + userId + " не владеет вещью с id=" + itemId);
		}
		itemRepository.deleteById(itemId);
		log.info("Удалена вещь c id={}", itemId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemDto> findItems(String text, int from, int size) {
		if (text.isBlank()) {
			return Collections.emptyList();
		}
		log.info("Запрошен поиск по тексту '{}'", text);
		return itemRepository.findByText(text, getPageable(from, size)).stream()
				.map(item -> toItemDto(item, null, null, null))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
		Optional<User> authorOptional = userRepository.findById(userId);
		if (authorOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		User author = authorOptional.get();
		Optional<Item> itemOptional = itemRepository.findById(itemId);
		if (itemOptional.isEmpty()) {
			throw new ItemNotFoundException("Вещь с id=" + itemId + " не найдена");
		}
		Item item = itemOptional.get();
		if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
			throw new BookingEndTimeException("Бронирование еще не завершилось или вы не владели этой вещью");
		}
		Comment comment = commentRepository.save(toComment(commentDto, item, author));
		log.info("Добавлен комментарий '{}'", comment);
		return toCommentDto(comment, author.getName());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemDto> getItemsByTemperatureIntervalInYourCity(Long userId, int from, int size) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
		}
		String city = userOptional.get().getCity();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String body = restTemplate.getForEntity(
					String.format(configuration.getLink(), city, configuration.getKey()),
					String.class
			).getBody();
			TemperatureIntervals temperatureInterval = temperatureMapper.getTemperatureInCelsiusFromString(body);
			if (temperatureInterval == TemperatureIntervals.ABNORMAL_TEMPERATURE) {
				throw new TemperatureDoesNotCorrespondToAnyTemperatureIntervalException("Аномальная температура");
			}
			log.info("Запрошен список вещей соответствующих температурному интервалу [" +
					temperatureInterval.getStartOfIntervalInclusive() + ";"
					+ temperatureInterval.getEndOfIntervalNotInclusive() + "]");
			return itemRepository.findAllByOwnerIdIsNotAndAvailableIsTrueAndRelevantTemperatureIntervalIsAndCityOrderByName(userId,
							temperatureInterval, city, getPageable(from, size)).stream()
					.map(this::toItemDtoInfo)
					.collect(Collectors.toList());
		} catch (RestClientException exception) {
			throw new UnableDetermineTemperatureException("Не удалось определить температуру в вашем городе, " +
					"проверьте правильно ли записан город в вашем профиле : " + city);
		}

	}

	private Pageable getPageable(int from, int size) {
		int page = from / size;
		return PageRequest.of(page, size);
	}

	private ItemDto ItemToItemDto(Item item) {
		LocalDateTime now = LocalDateTime.now();
		return toItemDto(
				item,
				bookingRepository.findFirstByItemIdAndBookingStatusNotAndStartBeforeOrderByStartDesc(
						item.getId(),
						BookingStatus.REJECTED,
						now
				),
				bookingRepository.findFirstByItemIdAndBookingStatusNotAndStartAfterOrderByStartAsc(
						item.getId(),
						BookingStatus.REJECTED,
						now

				),
				commentRepository.findByItemIdOrderByCreatedDesc(item.getId()).stream()
						.map(comment -> toCommentDto(comment, comment.getAuthor().getName()))
						.collect(Collectors.toList())
		);
	}

	private ItemDto toItemDtoInfo(Item item) {
		return toItemDto(
				item,
				null,
				null,
				commentRepository.findByItemIdOrderByCreatedDesc(item.getId()).stream()
						.map(comment -> toCommentDto(comment, comment.getAuthor().getName()))
						.collect(Collectors.toList())
		);
	}
}
