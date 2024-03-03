package ru.sber.shareit.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.sber.shareit.security.JwtGeneration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

	private final JwtGeneration jwtGeneration;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
			String jwtToken = authHeader.substring(7);
			if (jwtToken.isBlank()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Токен пуст");
			} else {
				try {
					String username = jwtGeneration.validateTokenAndGetClaim(jwtToken);
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);

					UsernamePasswordAuthenticationToken authToken =
							new UsernamePasswordAuthenticationToken(userDetails,
									userDetails.getPassword(),
									userDetails.getAuthorities());
					if (SecurityContextHolder.getContext().getAuthentication() == null) {
						SecurityContextHolder.getContext().setAuthentication(authToken);
					}
				} catch (JWTVerificationException exc) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Токен некорректный");
				}
			}
		}
		filterChain.doFilter(request, response);
	}
}
