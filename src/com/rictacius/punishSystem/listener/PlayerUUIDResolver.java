package com.rictacius.punishSystem.listener;

import java.net.InetAddress;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.rictacius.punishSystem.Main;

public class PlayerUUIDResolver implements Listener {
	private Main plugin = Main.pl;

	public PlayerUUIDResolver() {
	}

	@EventHandler
	public void onPing(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		InetAddress ip = p.getAddress().getAddress();
		plugin.getPlayersConfig().set("players." + p.getUniqueId(),
				ip.getHostAddress().replaceAll(Pattern.quote("."), "-"));
		plugin.savePlayersFile();
	}

	public static String resolveUUID(UUID uid) {
		String suid = Main.pl.getPlayersConfig().getString("players." + uid);
		return suid != null ? suid.replaceAll("-", ".") : "";
	}
}
