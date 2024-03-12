package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.entity.Comment;
import ru.sber.shareit.entity.Item;
import ru.sber.shareit.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentMapper {
	public static CommentDto commentToDto(Comment comment, String name) {
		return new CommentDto(
				comment.getId(),
				comment.getText(),
				name,
				comment.getCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
		);
	}

	public static Comment commentFromDto(CommentDto commentDto, Item item, User author) {
		return new Comment(
				commentDto.getId(),
				commentDto.getText(),
				item,
				author,
				LocalDateTime.now()
		);
	}
}
