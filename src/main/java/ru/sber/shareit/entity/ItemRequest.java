package ru.sber.shareit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "item_requests")
public class ItemRequest {
	@Id
	@Column(name = "item_request_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "description", length = 512, nullable = false)
	private String description;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requestor_id", nullable = false)
	private User requestor;
	private LocalDateTime created;
	@Transient
	private List<Item> items;
}
