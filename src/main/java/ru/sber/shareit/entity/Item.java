package ru.sber.shareit.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	private Long id;
	@Column(length = 127)
	private String name;
	@Column(length = 2000)
	private String description;
	@Column(name = "is_available")
	private Boolean available;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id")
	private ItemRequest request;
	@Column(name = "relevant_time_of_year")
	private TimeOfYear relevantTimeOfYear;
}