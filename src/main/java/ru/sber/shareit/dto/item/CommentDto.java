package ru.sber.shareit.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CommentDto {
	private Long id;
	@NotBlank(message = "Текст комментария не может быть пустым")
	private String text;
	private String authorName;
	private String created;
}

