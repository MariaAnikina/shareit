package ru.sber.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sber.shareit.entity.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

}