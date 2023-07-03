package net.novauniverse.playeraccountlib.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import net.novauniverse.playeraccountlib.PlayerAccountLib;
import net.novauniverse.playeraccountlib.account.Account;
import net.zeeraa.zcommandlib.command.ZCommand;
import net.zeeraa.zcommandlib.command.ZSubCommand;
import net.zeeraa.zcommandlib.command.utils.AllowedSenders;

public class AccountCommand extends ZCommand {
	private String commandName;

	public AccountCommand(String name) {
		super(name);
		this.commandName = name;

		setPermission("playeraccountlib.command.account");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addSubCommand(new Register());
		addSubCommand(new Delete());
		addSubCommand(new ResetPassword());

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String lable, String[] args) {
		Bukkit.dispatchCommand(sender, commandName + " help");
		return true;
	}
}

class Register extends ZSubCommand {
	public Register() {
		super("register");
		setPermission("playeraccountlib.command.account.register");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("Creates an account for you with the provided password");
		setEmptyTabMode(true);
		setFilterAutocomplete(false);
	}

	@Override
	public boolean execute(CommandSender sender, String lable, String[] args) {
		Player player = (Player) sender;
		if (PlayerAccountLib.getInstance().getStorageProvider().getAccount(player.getUniqueId()) != null) {
			sender.sendMessage(ChatColor.RED + "You already have an account");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You need to provide a password");
			return true;
		}

		String password = String.join(" ", args);
		UUID uuid = player.getUniqueId();
		UUID passwordSessionId = UUID.randomUUID();
		String username = player.getName().toLowerCase();
		
		String hash = BCrypt.hashpw(password, BCrypt.gensalt());
		
		Account account = new Account(uuid, passwordSessionId, username, hash, new JSONObject());
		
		account.save();
		
		sender.sendMessage(ChatColor.GREEN + "Account created");
		
		return true;
	}
}

class Delete extends ZSubCommand {
	public Delete() {
		super("delete");
		setPermission("playeraccountlib.command.account.delete");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("Deletes your account");
		setEmptyTabMode(true);
		setFilterAutocomplete(false);
	}

	@Override
	public boolean execute(CommandSender sender, String lable, String[] args) {
		Player player = (Player) sender;
		Account account = PlayerAccountLib.getInstance().getStorageProvider().getAccount(player.getUniqueId());
		if (account == null) {
			sender.sendMessage(ChatColor.RED + "You dont have an account");
			return true;
		}

		account.delete();

		sender.sendMessage(ChatColor.GREEN + "Account deleted");

		return true;
	}
}

class ResetPassword extends ZSubCommand {
	public ResetPassword() {
		super("resetpassword");
		setPermission("playeraccountlib.command.account.resetpassword");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("Sets the password of your account");
		setEmptyTabMode(true);
		setFilterAutocomplete(false);
	}

	@Override
	public boolean execute(CommandSender sender, String lable, String[] args) {
		Player player = (Player) sender;
		Account account = PlayerAccountLib.getInstance().getStorageProvider().getAccount(player.getUniqueId());
		if (account == null) {
			sender.sendMessage(ChatColor.RED + "You dont have an account");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You need to provide a password");
			return true;
		}

		String password = String.join(" ", args);

		account.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
		account.invalidateSessions();
		account.save();

		sender.sendMessage(ChatColor.GREEN + "Password changed");
		
		return true;
	}
}