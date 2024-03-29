package ru.sber.shareit.entity;


import lombok.*;
import ru.sber.shareit.entity.enums.TemperatureIntervals;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "items")
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	private Long id;
	private String name;
	private String description;
	@Column(name = "is_available")
	private Boolean available;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id")
	private ItemRequest request;
	@Enumerated(EnumType.STRING)
	@Column(name = "relevant_temperature_interval")
	private TemperatureIntervals relevantTemperatureInterval;
	private String city;
}