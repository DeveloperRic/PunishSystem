package com.rictacius.punishSystem.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.rictacius.punishSystem.Main;

import net.md_5.bungee.api.ChatColor;

public class TempBans {
	private int refreshTimer;

	public TempBans() {
		startTimer();
	}

	public void startTimer() {
		stopTimer();
		refreshTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.pl, new Runnable() {
			public void run() {
				Main.pl.reloadTempBans();
				List<String> tempbans = Main.pl.getTempbansConfig().getStringList("tempbans");
				List<String> newtemp = Main.pl.getTempbansConfig().getStringList("tempbans");
				for (int i = 0; i < tempbans.size(); i++) {
					String tempban = tempbans.get(i);
					String[] info = tempban.split("£");
					OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(info[0]));
					DateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm");
					Date bandate = null;
					try {
						bandate = format.parse(info[1]);
					} catch (ParseException e) {
						Bukkit.getConsoleSender().sendMessage(History.prefix + ChatColor.RED
								+ "Could not parse date for tempban of " + target.getName());
						continue;
					}
					Date date = new Date();
					if (bandate.before(date)) {
						newtemp.remove(tempban);
						if (Bukkit.getBanList(Type.NAME).isBanned(target.getName())) {
							Bukkit.getBanList(Type.NAME).pardon(target.getName());
							String autounban = ChatColor.translateAlternateColorCodes('&',
									Main.pl.getConfig().getString("autounban-message"));
							autounban = autounban.replaceAll("%target%", target.getName());
							Bukkit.broadcastMessage(autounban);
						}
					}
				}
				Main.pl.getTempbansConfig().set("tempbans", newtemp);
				Main.pl.saveTempBansFile();
			}
		}, 0L, 200L);
	}

	public void stopTimer() {
		Bukkit.getScheduler().cancelTask(refreshTimer);
	}
}
