package com.rictacius.punishSystem.menus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rictacius.punishSystem.Main;
import com.rictacius.punishSystem.utils.History;
import com.rictacius.punishSystem.utils.ValidItem;

import net.md_5.bungee.api.ChatColor;

public class HistoryMenu implements Listener {
	private static History history = Main.history;

	public static Inventory getHistoryMenu(Player player, OfflinePlayer target) {
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "PunishSystem | History ");
		int historyCount = history.getHistoryCount(target);
		int start = 1;
		if (historyCount > 54) {
			start = historyCount - 54;
		}
		int current = 0;
		Set<String> historyList = history.getHistory(target);
		for (String label : historyList) {
			current++;
			if (current < start) {
				continue;
			}
			ItemStack tempItem = new ItemStack(Material.INK_SACK);
			ItemMeta tempMeta = tempItem.getItemMeta();
			List<String> tempLore = new ArrayList<String>();
			String[] info = history.getHistoryInfo(target, label).split("£");
			if (info[0].startsWith("warn")) {
				tempItem.setDurability((short) 14);
				tempMeta.setDisplayName(ChatColor.YELLOW + "WARN by " + ChatColor.GOLD
						+ Bukkit.getOfflinePlayer(UUID.fromString(info[1])).getName());
				String date = info[2];
				tempLore.add(ChatColor.LIGHT_PURPLE + "Warned on " + date);
				tempLore.add(
						ChatColor.LIGHT_PURPLE + "Reason: " + ChatColor.translateAlternateColorCodes('&', info[3]));
				tempMeta.setLore(tempLore);
			} else if (info[0].startsWith("ban")) {
				tempItem.setDurability((short) 1);
				tempMeta.setDisplayName(ChatColor.RED + "BAN by " + ChatColor.GOLD
						+ Bukkit.getOfflinePlayer(UUID.fromString(info[1])).getName());
				String date = info[2];
				tempLore.add(ChatColor.LIGHT_PURPLE + "Banned on " + date);
				tempLore.add(
						ChatColor.LIGHT_PURPLE + "Reason: " + ChatColor.translateAlternateColorCodes('&', info[3]));
				int time = Integer.parseInt(info[4]);
				if (time >= 0) {
					SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm");
					String dateString = formatter.format(new Date(time * 1000L));
					tempLore.add(ChatColor.LIGHT_PURPLE + "Duration: " + dateString);
				} else {
					tempLore.add(ChatColor.LIGHT_PURPLE + "Duration: " + ChatColor.RED + "FOREVER");
				}
				tempMeta.setLore(tempLore);
			}
			tempItem.setItemMeta(tempMeta);
			inv.addItem(tempItem);
		}
		return inv;
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (ValidItem.invNameIs(event.getClickedInventory(), ChatColor.GOLD + "PunishSystem | History ")) {
			event.setCancelled(true);
		}
	}
}
