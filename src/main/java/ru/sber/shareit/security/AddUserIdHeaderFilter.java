package ru.sber.shareit.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sber.shareit.entity.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AddUserIdHeaderFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			UserDetailsImpl principalUser = (UserDetailsImpl) authentication.getPrincipal();
			response.addHeader("X-Sharer-User-Id", String.valueOf(principalUser.getUser().getId()));
			request.setAttribute("X-Sharer-User-Id", String.valueOf(principalUser.getUser().getId()));
		}
		filterChain.doFilter(request, response);
	}
}
