package net.novauniverse.playeraccountlib.account;

import java.util.UUID;

import org.json.JSONObject;

import net.novauniverse.playeraccountlib.PlayerAccountLib;

public class Account {
	private final UUID playerUuid;
	private UUID passwordSessionId;
	private String username;
	private String passwordHash;
	private JSONObject data;

	public Account(UUID playerUuid, UUID passwordSessionId, String username, String passwordHash, JSONObject data) {
		this.playerUuid = playerUuid;
		this.passwordSessionId = passwordSessionId;
		this.username = username;
		this.passwordHash = passwordHash;
		this.data = data;
	}

	public UUID getPlayerUuid() {
		return playerUuid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public JSONObject getData() {
		return data;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public UUID getPasswordSessionId() {
		return passwordSessionId;
	}

	public void invalidateSessions() {
		this.passwordSessionId = UUID.randomUUID();
	}

	public void save() {
		PlayerAccountLib.getInstance().getStorageProvider().saveAccount(this);
	}

	public void delete() {
		PlayerAccountLib.getInstance().getStorageProvider().deleteAccount(this);
	}
}