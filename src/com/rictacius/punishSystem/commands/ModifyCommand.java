package com.rictacius.punishSystem.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.rictacius.punishSystem.Main;
import com.rictacius.punishSystem.utils.History;
import com.rictacius.punishSystem.utils.PermCheck;

import net.md_5.bungee.api.ChatColor;

public class ModifyCommand implements CommandExecutor {
	private History history = Main.history;

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!PermCheck.senderHasAccess(sender, "punishsystem.modify")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.GOLD + "Incorrect Usage: (/psmodify <player> <immune | clear>)");
			return true;
		}
		OfflinePlayer toModify = null;
		try {
			toModify = Bukkit.getOfflinePlayer(args[0]);
			if (toModify == null) {
				sender.sendMessage(ChatColor.RED + "That player does not exisit!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/psmodify <player> <immune | clear>)");
				return true;
			}
			if (toModify.getUniqueId() == null) {
				sender.sendMessage(ChatColor.RED + "That player does not exisit!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/psmodify <player> <immune | clear>)");
				return true;
			}
			if (!toModify.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + "You can only modify players that have played before!");
				sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/psmodify <player> <immune | clear>)");
				return true;
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "That player does not exisit!");
			sender.sendMessage(ChatColor.GOLD + "Correct Usage: (/psmodify <player> <immune | clear>)");
			return true;
		}
		FileConfiguration config = history.getData(toModify);
		if (args[1].equalsIgnoreCase("immune")) {
			if (history.isImmune(toModify)) {
				config.set("immune", false);
				sender.sendMessage(ChatColor.AQUA + "Set " + toModify.getName() + " to not immune");
			} else {
				config.set("immune", true);
				sender.sendMessage(ChatColor.AQUA + "Set " + toModify.getName() + " to immune");
			}
			history.saveData(config, toModify);
		} else if (args[1].equalsIgnoreCase("clear")) {
			File file = history.getFile(toModify);
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				Bukkit.getConsoleSender().sendMessage(History.prefix + ChatColor.RED
						+ "Error: Could not clear player file for " + toModify.getName());
				e.printStackTrace();
			}
			config = history.getData(toModify);
			sender.sendMessage(ChatColor.AQUA + "Cleared " + toModify.getName() + "'s history");
		}
		return true;
	}

}
