package net.novauniverse.playeraccountlib.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.playeraccountlib.PlayerAccountLib;

public class Listeners implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		PlayerAccountLib.getInstance().getStorageProvider().handlePlayerJoin(e);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerAccountLib.getInstance().getStorageProvider().handlePlayerQuit(e);
	}
}