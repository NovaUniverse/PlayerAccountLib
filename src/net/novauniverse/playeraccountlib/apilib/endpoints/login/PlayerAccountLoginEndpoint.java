package net.novauniverse.playeraccountlib.apilib.endpoints.login;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.endpoint.HTTPEndpoint;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.enums.StandardResponseType;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.TextResponse;
import net.novauniverse.playeraccountlib.PlayerAccountLib;
import net.novauniverse.playeraccountlib.account.Account;

public class PlayerAccountLoginEndpoint extends HTTPEndpoint {
	public PlayerAccountLoginEndpoint() {
		setAllowedMethods(HTTPMethod.POST);
		setStandardResponseType(StandardResponseType.TEXT);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject body;
		try {
			body = new JSONObject(request.getBody());
		} catch (JSONException e) {
			return new TextResponse("Bad request: Invalid json body", HTTPResponseCode.BAD_REQUEST);
		}

		if (!body.has("username")) {
			return new TextResponse("Bad request: Missing body parameter: username", HTTPResponseCode.BAD_REQUEST);
		}

		if (!body.has("password")) {
			return new TextResponse("Bad request: Missing body parameter: password", HTTPResponseCode.BAD_REQUEST);
		}

		String username = body.getString("username");
		String password = body.getString("password");

		Account account = PlayerAccountLib.getInstance().getStorageProvider().getAccount(username.toLowerCase());

		if (account == null) {
			return new TextResponse("Wrong username or password", HTTPResponseCode.FORBIDDEN);
		}

		if (!BCrypt.checkpw(password, account.getPasswordHash())) {
			return new TextResponse("Wrong username or password", HTTPResponseCode.FORBIDDEN);
		}

		String token = PlayerAccountLib.getInstance().getJwtUtil().sign(account);
		return new TextResponse(token);
	}

}
