package net.novauniverse.playeraccountlib.jwt;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import net.novauniverse.playeraccountlib.PlayerAccountLib;
import net.novauniverse.playeraccountlib.account.Account;

public class JWTUtil {
	public static final String ISSUER = "playeraccountlib";

	private Algorithm algorithm;

	public JWTUtil(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
		algorithm = Algorithm.RSA256(publicKey, privateKey);
	}

	public String sign(Account account) {
		JWTCreator.Builder builder = JWT.create();
		builder.withIssuer(ISSUER);
		builder.withClaim("p_uuid", account.getPlayerUuid().toString());
		builder.withClaim("p_username", account.getUsername());
		builder.withClaim("password_session", account.getPasswordSessionId().toString());
		return builder.sign(algorithm);
	}

	public Account decode(String token) {
		try {
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();

			DecodedJWT decodedJWT = verifier.verify(token);

			UUID uuid = UUID.fromString(decodedJWT.getClaim("p_uuid").asString());
			Account account = PlayerAccountLib.getInstance().getStorageProvider().getAccount(uuid);

			if (account.getPasswordSessionId().toString().equalsIgnoreCase(decodedJWT.getClaim("password_session").asString())) {
				return account;
			}
		} catch (JWTVerificationException exception) {
			exception.printStackTrace();
		}
		return null;
	}
}