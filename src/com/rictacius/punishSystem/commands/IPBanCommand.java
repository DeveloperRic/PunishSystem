package com.rictacius.punishSystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rictacius.punishSystem.Main;
import com.rictacius.punishSystem.listener.PlayerUUIDResolver;
import com.rictacius.punishSystem.menus.GUIBanIPInput;
import com.rictacius.punishSystem.utils.PermCheck;

import net.md_5.bungee.api.ChatColor;

public class IPBanCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!PermCheck.senderHasAccess(sender, "punishsystem.punish")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		if (!PermCheck.senderHasAccess(sender, "punishsystem.ipban")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use the ipban command!");
			return true;
		}
		OfflinePlayer toBan = null;
		try {
			toBan = Bukkit.getOfflinePlayer(args[0]);
			if (toBan == null) {
				sender.sendMessage(ChatColor.RED + "That player does not exisit!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/ban <player> <reason>)");
				return true;
			}
			if (toBan.getUniqueId() == null) {
				sender.sendMessage(ChatColor.RED + "That player does not exisit!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/ban <player> <reason>)");
				return true;
			}
			if (!toBan.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + "You can only punish players that have played before!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/ban <player> <reason>)");
				return true;
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/ipban <player> <reason>)");
			return true;
		}
		if (Main.history.isImmune(toBan) && !PermCheck.senderHasAccess(sender, "punishsystem.punishimmune")) {
			sender.sendMessage(ChatColor.RED + "You may not punish that player! That player is immune to punnisments.");
			return true;
		}
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/ipban <player> <reason>)");
				return true;
			}
			String reason = args[1];
			if (args.length > 2) {
				StringBuilder strBuilder = new StringBuilder();
				for (int i = 2; i < args.length; i++) {
					strBuilder.append(" " + args[i]);
				}
				reason = reason + strBuilder.toString();
			}
			GUIBanIPInput.doTask(toBan, reason, "@Console", PlayerUUIDResolver.resolveUUID(toBan.getUniqueId()));
		} else {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/ipban <player> <reason>)");
				return true;
			}
			String reason = args[1];
			if (args.length > 2) {
				StringBuilder strBuilder = new StringBuilder();
				for (int i = 2; i < args.length; i++) {
					strBuilder.append(" " + args[i]);
				}
				reason = reason + strBuilder.toString();
			}
			GUIBanIPInput.doTask(toBan, reason, ((Player) sender).getUniqueId().toString(),
					PlayerUUIDResolver.resolveUUID(toBan.getUniqueId()));
		}
		return true;
	}
}
