package com.rictacius.punishSystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.rictacius.punishSystem.Main;
import com.rictacius.punishSystem.menus.MainMenu;
import com.rictacius.punishSystem.utils.History;
import com.rictacius.punishSystem.utils.PermCheck;

import net.md_5.bungee.api.ChatColor;

public class PunishCommand implements CommandExecutor {
	private History history = Main.history;

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Command only useable by players!");
			return true;
		}
		if (!PermCheck.senderHasAccess(sender, "punishsystem.punish")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "You must specify a player to punish!");
			player.sendMessage(ChatColor.GOLD + "Correct Usage: (/punish <player>)");
			return true;
		}
		OfflinePlayer toPunish = null;
		try {
			toPunish = Bukkit.getOfflinePlayer(args[0]);
			if (toPunish == null) {
				player.sendMessage(ChatColor.RED + "That player does not exisit!");
				player.sendMessage(ChatColor.GOLD + "Correct Usage: (/punish <player>)");
				return true;
			}
			if (toPunish.getUniqueId() == null) {
				player.sendMessage(ChatColor.RED + "That player does not exisit!");
				player.sendMessage(ChatColor.GOLD + "Correct Usage: (/punish <player>)");
				return true;
			}
			if (!toPunish.hasPlayedBefore()) {
				player.sendMessage(ChatColor.RED + "You can only punish players that have played before!");
				player.sendMessage(ChatColor.GOLD + "Correct Usage: (/punish <player>)");
				return true;
			}
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "That player does not exisit!");
			player.sendMessage(ChatColor.GOLD + "Correct Usage: (/punish <player>)");
			return true;
		}
		if (history.isImmune(toPunish) && !PermCheck.hasPerm(player, "punishsystem.punishimmune")) {
			player.sendMessage(ChatColor.RED + "You may not punish that player! That player is immune to punnisments.");
			return true;
		}
		player.openInventory(MainMenu.getMainMenu(player, toPunish));
		return true;
	}

}
