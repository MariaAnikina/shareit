package ru.sber.shareit.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sber.shareit.entity.Item;
import ru.sber.shareit.entity.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
	List<ItemRequest> findByRequestorIdNotAndRequestorCityIsOrderByCreatedDesc(Long userId, String city, Pageable pageable);
	List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long userId, Pageable pageable);

	List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long userId);

}