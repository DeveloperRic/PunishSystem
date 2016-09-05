package com.rictacius.punishSystem.utils;

import org.bukkit.OfflinePlayer;

import com.rictacius.punishSystem.Main;

import net.md_5.bungee.api.ChatColor;

public class ConfigParser {
	private static Main plugin = Main.pl;

	public static String sourceTargetParse(String path, String source, OfflinePlayer target) {
		String raw = plugin.getConfig().getString(path);
		raw = ChatColor.translateAlternateColorCodes('&', raw);
		raw = raw.replaceAll("%source%", source);
		raw = raw.replaceAll("%target%", target.getName());
		return raw;
	}

	public static String appendSourceTargetParse(String path, String source, OfflinePlayer target,
			String[] args) {
		String raw = plugin.getConfig().getString(path);
		raw = ChatColor.translateAlternateColorCodes('&', raw);
		raw = raw.replaceAll("%source%", source);
		raw = raw.replaceAll("%target%", target.getName());
		for (int i = 0; i < args.length; i++) {
			if (i % 2 != 0) {
				raw = raw.replaceAll(args[i - 1], args[i]);
			}
		}
		return raw;
	}
}
