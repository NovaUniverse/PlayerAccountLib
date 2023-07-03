package net.novauniverse.playeraccountlib.storageprovider;

import java.util.UUID;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.playeraccountlib.account.Account;

public interface StorageProvider {
	boolean hasAccount(String username);

	boolean hasAccount(UUID uuid);

	Account getAccount(String username);

	Account getAccount(UUID uuid);

	boolean saveAccount(Account account);

	boolean deleteAccount(Account account);
	
	void init();
	
	void handlePlayerJoin(PlayerJoinEvent e);
	
	void handlePlayerQuit(PlayerQuitEvent e);
}