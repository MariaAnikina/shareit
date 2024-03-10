package ru.sber.shareit.util.mapper;

import ru.sber.shareit.dto.item.CommentDto;
import ru.sber.shareit.entity.Comment;
import ru.sber.shareit.entity.Item;
import ru.sber.shareit.entity.User;

import java.time.LocalDateTime;

public class CommentMapper {
	public static CommentDto commentToDto(Comment comment, String name) {
		return new CommentDto(
				comment.getId(),
				comment.getText(),
				name,
				comment.getCreated()
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
