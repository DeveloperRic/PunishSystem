package com.rictacius.punishSystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rictacius.punishSystem.Main;
import com.rictacius.punishSystem.menus.GUIWarnInput;
import com.rictacius.punishSystem.utils.PermCheck;

import net.md_5.bungee.api.ChatColor;

public class WarnCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!PermCheck.senderHasAccess(sender, "punishsystem.punish")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		if (!PermCheck.senderHasAccess(sender, "punishsystem.warn")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use the warn command!");
			return true;
		}
		OfflinePlayer toWarn = null;
		try {
			toWarn = Bukkit.getOfflinePlayer(args[0]);
			if (toWarn == null) {
				sender.sendMessage(ChatColor.RED + "That player does not exisit!");
				sender.sendMessage(ChatColor.GOLD + "Incorrect Usage: (/warn <player> <reason>)");
				return true;
			}
			if (toWarn.getUniqueId() == null) {
				sender.sendMessage(ChatColor.RED + "That player does not exisit!");
				sender.sendMessage(ChatColor.GOLD + "Incorrect Usage: (/warn <player> <reason>)");
				return true;
			}
			if (!toWarn.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + "You can only punish players that have played before!");
				sender.sendMessage(ChatColor.GOLD + "Incorrect Usage: (/warn <player> <reason>)");
				return true;
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "That player does not exisit!");
			sender.sendMessage(ChatColor.GOLD + "Incorrect Usage: (/warn <player> <reason>)");
			return true;
		}
		if (Main.history.isImmune(toWarn) && !PermCheck.senderHasAccess(sender, "punishsystem.punishimmune")) {
			sender.sendMessage(ChatColor.RED + "You may not punish that player! That player is immune to punnisments.");
			return true;
		}
		if (!(sender instanceof Player)) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.GOLD + "Incorrect Usage: (/warn <player> <reason>)");
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
			GUIWarnInput.doTask(toWarn, reason, "@Console");
		} else {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.GOLD + "Incorrect Usage: (/warn <player> <reason>)");
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
			GUIWarnInput.doTask(toWarn, reason, ((Player) sender).getUniqueId().toString());
		}
		return true;
	}
}
