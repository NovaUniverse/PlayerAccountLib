package net.novauniverse.playeraccountlib.storageprovider.providers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.playeraccountlib.PlayerAccountLib;
import net.novauniverse.playeraccountlib.account.Account;
import net.novauniverse.playeraccountlib.storageprovider.StorageProvider;

public class FileStorageProvider implements StorageProvider {
	private File dataFolder;
	private Map<String, UUID> usernameCache;

	public FileStorageProvider(File dataFolder) {
		this.dataFolder = dataFolder;
		this.usernameCache = new HashMap<>();

		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
	}

	@Override
	public void init() {
		File[] files = dataFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		});
		for (File file : files) {
			try {
				Account account = this.parse(file);
				usernameCache.put(account.getUsername().toLowerCase(), account.getPlayerUuid());
			} catch (Exception e) {
				PlayerAccountLib.getInstance().getLogger().log(Level.WARNING, "Failed to read account data from file " + file.getAbsolutePath() + ". " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private Account parse(File file) throws JSONException, IOException {
		JSONObject json = new JSONObject(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
		UUID uuid = UUID.fromString(json.getString("uuid"));
		UUID passwordSessionId = UUID.fromString(json.getString("password_session_id"));
		String username = json.getString("username");
		String passwordHash = json.getString("password");
		JSONObject data = json.optJSONObject("data", new JSONObject());

		return new Account(uuid, passwordSessionId, username, passwordHash, data);
	}

	public File getFileByUUID(UUID uuid) {
		return new File(dataFolder.getAbsolutePath() + File.separator + uuid.toString() + ".json");
	}

	@Override
	public boolean hasAccount(String username) {
		return usernameCache.containsKey(username.toLowerCase());
	}

	@Override
	public boolean hasAccount(UUID uuid) {
		return getAccount(uuid) != null;
	}

	@Override
	public Account getAccount(String username) {
		if (usernameCache.containsKey(username)) {
			UUID uuid = usernameCache.get(username);
			return getAccount(uuid);
		}
		return null;
	}

	@Override
	public Account getAccount(UUID uuid) {
		File file = this.getFileByUUID(uuid);
		if (file.exists()) {
			try {
				return parse(file);
			} catch (Exception e) {
				PlayerAccountLib.getInstance().getLogger().log(Level.WARNING, "Failed to parse account data from file " + file.getAbsolutePath() + ". " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public boolean saveAccount(Account account) {
		JSONObject data = new JSONObject();

		data.put("uuid", account.getPlayerUuid().toString());
		data.put("password_session_id", account.getPasswordSessionId());
		data.put("username", account.getUsername());
		data.put("password", account.getPasswordHash());
		data.put("data", account.getData());

		File file = this.getFileByUUID(account.getPlayerUuid());
		String content = data.toString(4);

		usernameCache.put(account.getUsername(), account.getPlayerUuid());

		try {
			FileUtils.write(file, content, StandardCharsets.UTF_8, false);
			return true;
		} catch (Exception e) {
			PlayerAccountLib.getInstance().getLogger().log(Level.WARNING, "Failed to save account data to file " + file.getAbsolutePath() + ". " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteAccount(Account account) {
		File file = getFileByUUID(account.getPlayerUuid());
		return file.delete();
	}

	@Override
	public void handlePlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		Account account = getAccount(player.getUniqueId());
		if (account != null) {
			if (!account.getUsername().equalsIgnoreCase(player.getName())) {
				account.setUsername(player.getName());
				account.save();
			}
		}
	}

	@Override
	public void handlePlayerQuit(PlayerQuitEvent e) {
	}
}