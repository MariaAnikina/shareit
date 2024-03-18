package ru.sber.shareit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sber.shareit.entity.Item;
import ru.sber.shareit.entity.enums.TemperatureIntervals;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findByOwnerId(Long userId, Pageable pageable);
	List<Item> findByCity(String city, Pageable pageable);

	@Query("select it " +
			"from Item as it " +
			"where (lower(it.name) like lower(concat('%', ?1,'%')) " +
			"or lower(it.description) like lower(concat('%', ?1,'%'))) " +
			"and it.available = TRUE")
	List<Item> findByText(String text, Pageable pageable);

	Page<Item> findAllByOwnerIdIsNotAndAvailableIsTrueAndRelevantTemperatureIntervalIsAndCityOrderByName(
			Long owner_id, TemperatureIntervals relevantTemperatureInterval, String city, Pageable pageable);

	List<Item> findByRequestId(Long id);
}
