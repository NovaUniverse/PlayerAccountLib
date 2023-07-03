package net.novauniverse.playeraccountlib.apilib.authprovider;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.auth.AuthenticationProvider;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.playeraccountlib.PlayerAccountLib;
import net.novauniverse.playeraccountlib.account.Account;

public class PlayerAccountAuthProvider implements AuthenticationProvider {
	@Override
	public Authentication authenticate(Request request) {
		String token = null;

		String auth = request.getFirstRequestHeader("authorization");
		if (auth != null) {
			if (auth.length() > 0) {
				String[] authParts = auth.split(" ");
				token = authParts[authParts.length - 1];
			}
		}

		if (token != null) {
			if (request.getQueryParameters().containsKey("token")) {
				token = request.getQueryParameters().get("token");
			}
		}

		if (token != null) {
			Account account = PlayerAccountLib.getInstance().getJwtUtil().decode(token);
			if (account != null) {
				return new PlayerAccountAuth(account);
			}
		}

		return null;
	}
}