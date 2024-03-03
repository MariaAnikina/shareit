package ru.sber.shareit.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtGeneration {

	@Value("{$JWT_secret}")
	private String secret;

	public String generationToken(String username) {
		Date tokenExpirationDate = Date.from(ZonedDateTime.now().plusHours(2).toInstant());

		return JWT.create()
				.withSubject("Person details")
				.withClaim("username", username)
				.withIssuedAt(new Date())
				.withIssuer("maria-anikina")
				.withExpiresAt(tokenExpirationDate)
				.sign(Algorithm.HMAC256(secret));
	}

	public String validateTokenAndGetClaim(String token) throws JWTVerificationException {
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
				.withSubject("Person details")
				.withIssuer("maria-anikina")
				.build();
		DecodedJWT jwt = verifier.verify(token);
		return jwt.getClaim("username").asString();
	}
}
