package ru.sber.shareit.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long id;
	@Column(length = 1000)
	private String text;
	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;
	private LocalDateTime created;
}
