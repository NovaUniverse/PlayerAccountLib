package net.novauniverse.playeraccountlib.apilib.authprovider;

import java.util.ArrayList;
import java.util.List;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.playeraccountlib.account.Account;

public class PlayerAccountAuth extends Authentication {
	private final Account account;

	public PlayerAccountAuth(Account account) {
		this.account = account;
	}

	public Account getAccount() {
		return account;
	}

	@Override
	public List<String> getPermissions() {
		return new ArrayList<>();
	}
}