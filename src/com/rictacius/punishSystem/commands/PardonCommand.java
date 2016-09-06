package com.rictacius.punishSystem.commands;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.rictacius.punishSystem.listener.PlayerUUIDResolver;
import com.rictacius.punishSystem.utils.PermCheck;

import net.md_5.bungee.api.ChatColor;

public class PardonCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!PermCheck.senderHasAccess(sender, "punishsystem.punish")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		if (!PermCheck.senderHasAccess(sender, "punishsystem.pardon")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use the pardon command!");
			return true;
		}
		OfflinePlayer toPardon = null;
		try {
			toPardon = Bukkit.getOfflinePlayer(args[0]);
			if (toPardon == null) {
				sender.sendMessage(ChatColor.RED + "That player does not exisit!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/pardon <player>)");
				return true;
			}
			if (toPardon.getUniqueId() == null) {
				sender.sendMessage(ChatColor.RED + "That player does not exisit!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/pardon <player>)");
				return true;
			}
			if (!toPardon.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + "You can only pardon players that have played before!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/pardon <player>)");
				return true;
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "That player does not exisit!");
			sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/pardon <player>)");
			return true;
		}
		Bukkit.getBanList(Type.NAME).pardon(toPardon.getName());
		sender.sendMessage(ChatColor.GREEN + "Pardoned " + toPardon.getName());
		String address = PlayerUUIDResolver.resolveUUID(toPardon.getUniqueId());
		Bukkit.getBanList(Type.IP).pardon(address);
		sender.sendMessage(address.equals("") ? ChatColor.RED + "Did not pardon " + toPardon.getName() + "'s IP"
				: ChatColor.GREEN + "Pardoned " + toPardon.getName() + "'s IP");
		return true;
	}

}
