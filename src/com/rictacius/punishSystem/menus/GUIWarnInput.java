package com.rictacius.punishSystem.menus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.BanList.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.rictacius.punishSystem.Main;
import com.rictacius.punishSystem.utils.ConfigParser;
import com.rictacius.punishSystem.utils.SkIP;

public class GUIWarnInput implements Listener {
	private static HashMap<UUID, UUID> waiting = new HashMap<UUID, UUID>();

	public static void requestWarnInput(final Player sender, OfflinePlayer target) {
		sender.closeInventory();
		if (!waiting.containsKey(sender.getUniqueId())) {
			waiting.put(sender.getUniqueId(), target.getUniqueId());
		}
		sender.sendMessage(ConfigParser.sourceTargetParse("requests.warninput-message", sender.getName(), target));
	}

	@EventHandler
	public void onClick(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		if (waiting.containsKey(p.getUniqueId())) {
			event.setCancelled(true);
			String message = event.getMessage().replaceAll("£", "");
			OfflinePlayer target = Bukkit.getOfflinePlayer(waiting.get(p.getUniqueId()));
			waiting.remove(p.getUniqueId());
			if (message.equalsIgnoreCase("@cancel")) {
				p.sendMessage(ConfigParser.sourceTargetParse("requests.canceled-message", p.getName(), target));
				p.openInventory(MainMenu.getMainMenu(p, target));
				return;
			}
			doTask(target, message, p.getUniqueId().toString());
		}
	}

	public static void doTask(final OfflinePlayer target, final String message, final String uidSource) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
			public void run() {
				int warns = Main.history.getWarnCount(target);
				int base = Main.history.getMaxWarns();
				if (warns < base - 1) {
					String[] args = new String[2];
					args[0] = "%reason%";
					args[1] = message;
					Main.history.warnPlayer(target, message, uidSource);
					if (!uidSource.equals("@Console")) {
						Bukkit.getPlayer(UUID.fromString(uidSource)).sendMessage(ConfigParser
								.appendSourceTargetParse("source-responses.warned-player", uidSource, target, args));
					} else {
						Bukkit.getConsoleSender().sendMessage(ConfigParser
								.appendSourceTargetParse("source-responses.warned-player", uidSource, target, args));
					}
					String kick = ConfigParser.appendSourceTargetParse("target-responses.warned-player", uidSource,
							target, args);
					if (kick.contains("%n%")) {
						String[] lines = kick.split("%n%");
						kick = lines[0];
						for (int i = 1; i < lines.length; i++) {
							String s = ChatColor.getLastColors(kick);
							kick = kick + " \n " + s + lines[i];
						}
					}
					if (target.isOnline()) {
						target.getPlayer().kickPlayer(kick);
					}
					Bukkit.broadcastMessage(
							ConfigParser.appendSourceTargetParse("warn-message", uidSource, target, args));
				} else if (warns == base - 1) {
					Main.history.banPlayer(target, message, uidSource, 86400);
					SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm");
					long dateInMillis = System.currentTimeMillis() + (86400 * 1000);
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(dateInMillis);
					try {
						calendar.setTimeZone(TimeZone.getTimeZone(
								SkIP.getIPData(target.getPlayer().getAddress().getHostName()).getTimezone()));
					} catch (Exception e) {
					}
					String dateString = formatter.format(calendar.getTime());
					String[] args = new String[4];
					args[0] = "%reason%";
					args[1] = message;
					args[2] = "%expiration%";
					args[3] = dateString;
					String kick = ConfigParser.appendSourceTargetParse("target-responses.temp-banned-player", uidSource,
							target, args);
					if (kick.contains("%n%")) {
						String[] lines = kick.split("%n%");
						kick = lines[0];
						for (int i = 1; i < lines.length; i++) {
							String s = ChatColor.getLastColors(kick);
							kick = kick + " \n " + s + lines[i];
						}
					}
					if (target.isOnline()) {
						target.getPlayer().kickPlayer(kick);
					}
					Bukkit.getBanList(Type.NAME).addBan(target.getName(), kick, calendar.getTime(), null);
					if (!uidSource.equals("@Console")) {
						Bukkit.getPlayer(UUID.fromString(uidSource)).sendMessage(ConfigParser.appendSourceTargetParse(
								"source-responses.temp-banned-player", uidSource, target, args));
					} else {
						Bukkit.getConsoleSender().sendMessage(ConfigParser.appendSourceTargetParse(
								"source-responses.temp-banned-player", uidSource, target, args));
					}
					Bukkit.broadcastMessage(
							ConfigParser.appendSourceTargetParse("tempban-message", uidSource, target, args));
					List<String> tempbans = Main.pl.getTempbansConfig().getStringList("tempbans");
					tempbans.add(target.getUniqueId() + "£" + dateString);
					Main.pl.getTempbansConfig().set("tempbans", tempbans);
					Main.pl.saveTempBansFile();
					return;
				} else if (warns >= base) {
					GUIBanInput.doTask(target, message, uidSource);
				}
			}
		}, 0L);
	}
}
