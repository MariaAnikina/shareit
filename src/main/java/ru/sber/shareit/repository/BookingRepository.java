package ru.sber.shareit.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sber.shareit.entity.Booking;
import ru.sber.shareit.entity.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	Booking findFirstByItemIdAndBookingStatusNotAndStartBeforeOrderByStartDesc(Long itemId, BookingStatus status,
	                                                                           LocalDateTime localDateTime);

	Booking findFirstByItemIdAndBookingStatusNotAndStartAfterOrderByStartAsc(Long itemId, BookingStatus status,
	                                                                         LocalDateTime localDateTime);

	Boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime localDateTime);

	Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long userId);

	List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime localDateTime,
	                                                          Pageable pageable);

	List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime forStart,
	                                                                      LocalDateTime forEnd, Pageable pageable);

	List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime,
	                                                         Pageable pageable);

	List<Booking> findByBookerIdAndBookingStatusOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

	List<Booking> findByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

	List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

	List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

	List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

	List<Booking> findByItemOwnerIdAndBookingStatusOrderByStartDesc(Long userId, BookingStatus waiting, Pageable pageable);

	List<Booking> findByItemOwnerIdOrderByStartDesc(Long userId, Pageable pageable);
}
