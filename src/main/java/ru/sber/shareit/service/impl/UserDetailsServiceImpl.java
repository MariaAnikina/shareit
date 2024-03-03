package ru.sber.shareit.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sber.shareit.entity.User;
import ru.sber.shareit.repository.UserRepository;
import ru.sber.shareit.security.UserDetailsImpl;

import java.util.Optional;

@Service
@Setter
@Getter
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = repository.findByUsername(username);
		if (user.isEmpty()) {
			throw new UsernameNotFoundException("Пользователь не найден");
		}
		return new UserDetailsImpl(user.get());
	}
}
