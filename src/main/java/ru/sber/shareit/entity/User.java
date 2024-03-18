package ru.sber.shareit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.sber.shareit.entity.enums.Role;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Component
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;
	@Column(name = "username", length = 512, unique = true, nullable = false)
	private String username;
	@Column(name = "password", length = 512, nullable = false)
	private String password;
	@Column(name = "name", length = 512)
	private String name;
	@Column(name = "email", length = 512, unique = true, nullable = false)
	private String email;
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;
	@Column(name = "city", length = 256)
	private String city;
}