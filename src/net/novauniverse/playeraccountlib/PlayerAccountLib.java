package net.novauniverse.playeraccountlib;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import net.novauniverse.playeraccountlib.command.AccountCommand;
import net.novauniverse.playeraccountlib.jwt.JWTUtil;
import net.novauniverse.playeraccountlib.listeners.Listeners;
import net.novauniverse.playeraccountlib.rsa.RSAPair;
import net.novauniverse.playeraccountlib.rsa.RSAUtil;
import net.novauniverse.playeraccountlib.storageprovider.StorageProvider;
import net.novauniverse.playeraccountlib.storageprovider.providers.FileStorageProvider;
import net.zeeraa.zcommandlib.command.registrator.ZCommandRegistrator;

public class PlayerAccountLib extends JavaPlugin {
	private static PlayerAccountLib instance;
	private StorageProvider storageProvider;
	private JWTUtil jwtUtil;
	private Listeners listeners;

	public static PlayerAccountLib getInstance() {
		return instance;
	}

	public StorageProvider getStorageProvider() {
		return storageProvider;
	}

	public JWTUtil getJwtUtil() {
		return jwtUtil;
	}

	@Override
	public void onEnable() {
		PlayerAccountLib.instance = this;

		saveDefaultConfig();

		listeners = new Listeners();

		File keyFolder = new File(getDataFolder().getAbsolutePath() + File.separator + "keys");
		if (!keyFolder.exists()) {
			keyFolder.mkdir();
		}

		File publicKeyFile = new File(keyFolder.getAbsolutePath() + File.separator + "rsa.pub");
		File privateKeyFile = new File(keyFolder.getAbsolutePath() + File.separator + "rsa.key");

		RSAPublicKey publicKey;
		RSAPrivateKey privateKey;

		if (!publicKeyFile.exists() || !privateKeyFile.exists()) {
			getLogger().info("Creating RSA keys");
			try {
				RSAPair pair = RSAUtil.generarePair();

				publicKey = pair.getPublicKey();
				privateKey = pair.getPrivateKey();

				RSAUtil.writeKeyToFile(publicKeyFile.getAbsolutePath(), publicKey.getEncoded());
				RSAUtil.writeKeyToFile(privateKeyFile.getAbsolutePath(), privateKey.getEncoded());
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
		} else {
			getLogger().info("Reading RSA keys");
			try {
				publicKey = RSAUtil.readPublicKeyFromFile(publicKeyFile.getAbsolutePath());
				privateKey = RSAUtil.readPrivateKeyFromFile(privateKeyFile.getAbsolutePath());
			} catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
		}

		jwtUtil = new JWTUtil(publicKey, privateKey);

		String providerName = getConfig().getString("StorageProvider").toLowerCase();
		switch (providerName) {
		case "file":
			getLogger().log(Level.INFO, "Using file based storage provider. Data folder: " + providerName);
			File accountDatafolder = new File(getDataFolder().getAbsolutePath() + File.separator + "AccountData");
			storageProvider = new FileStorageProvider(accountDatafolder);
			break;

		default:
			getLogger().log(Level.SEVERE, "Invalid storage provider: " + providerName + ". Please set a valid storage provider in config.yml");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		storageProvider.init();

		Bukkit.getPluginManager().registerEvents(listeners, this);

		AccountCommand command = new AccountCommand(getConfig().getString("AccountCommandName"));
		ZCommandRegistrator.registerCommand(this, command);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(listeners);
	}
}