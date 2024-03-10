package ru.sber.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sber.shareit.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByItemIdOrderByCreatedDesc(Long itemId);

}

