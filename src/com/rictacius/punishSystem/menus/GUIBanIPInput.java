package com.rictacius.punishSystem.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.rictacius.punishSystem.Main;
import com.rictacius.punishSystem.utils.ConfigParser;

public class GUIBanIPInput implements Listener {
	private static HashMap<UUID, ArrayList<Object>> waiting = new HashMap<UUID, ArrayList<Object>>();

	public static void requestBanInput(Player sender, OfflinePlayer target, String address) {
		sender.closeInventory();
		if (!waiting.containsKey(sender.getUniqueId())) {
			ArrayList<Object> e = new ArrayList<Object>();
			e.add(target.getUniqueId());
			e.add(address);
			waiting.put(sender.getUniqueId(), e);
		}
		sender.sendMessage(ConfigParser.sourceTargetParse("requests.baninput-message", sender.getName(), target));
	}

	@EventHandler
	public void onClick(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		if (waiting.containsKey(p.getUniqueId())) {
			event.setCancelled(true);
			String message = event.getMessage().replaceAll("£", "");
			OfflinePlayer target = Bukkit.getOfflinePlayer((UUID) waiting.get(p.getUniqueId()).get(0));
			String address = (String) waiting.get(p.getUniqueId()).get(1);
			waiting.remove(p.getUniqueId());
			if (message.equalsIgnoreCase("@cancel")) {
				p.sendMessage(ConfigParser.sourceTargetParse("requests.canceled-message", p.getName(), target));
				p.openInventory(MainMenu.getMainMenu(p, target));
				return;
			}
			doTask(target, message, p.getUniqueId().toString(), address);
		}
	}

	public static void doTask(final OfflinePlayer target, final String message, final String uidSource,
			final String address) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
			public void run() {
				String[] args = new String[2];
				args[0] = "%reason%";
				args[1] = message;
				String kick = ConfigParser.appendSourceTargetParse("target-responses.banned-player", uidSource, target,
						args);
				kick = kick.replaceAll("ban", "ipban").replaceAll("BAN", "IPBAN").replaceAll("Ban", "IPBan");
				if (kick.contains("%n%")) {
					String[] lines = kick.split("%n%");
					kick = lines[0];
					for (int i = 1; i < lines.length; i++) {
						String s = ChatColor.getLastColors(kick);
						kick = kick + " \n " + s + lines[i];
					}
				}
				Main.history.ipBanPlayer(target, message, uidSource, -999);
				Bukkit.getBanList(Type.IP).addBan(address, kick, null, null);
				if (!uidSource.equals("@Console")) {
					Bukkit.getPlayer(UUID.fromString(uidSource)).sendMessage(ConfigParser
							.appendSourceTargetParse("source-reponses.banned-player", uidSource, target, args));
				} else {
					Bukkit.getConsoleSender().sendMessage(ConfigParser
							.appendSourceTargetParse("source-reponses.banned-player", uidSource, target, args));
				}
				if (target.isOnline()) {
					target.getPlayer().kickPlayer(kick);
				}
				Bukkit.broadcastMessage(ConfigParser.appendSourceTargetParse("ban-message", uidSource, target, args));
			}
		}, 0L);
	}
}
