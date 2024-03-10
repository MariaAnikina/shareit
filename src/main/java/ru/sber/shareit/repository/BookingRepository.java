package ru.sber.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sber.shareit.entity.Booking;
import ru.sber.shareit.entity.BookingStatus;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	Booking findFirstByItemIdAndBookingStatusNotAndStartBeforeOrderByStartDesc(Long itemId, BookingStatus rejected,
	                                                                           LocalDateTime now);

	Booking findFirstByItemIdAndBookingStatusNotAndStartAfterOrderByStartAsc(Long itemId, BookingStatus rejected,
	                                                                         LocalDateTime now);

}
