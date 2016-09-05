package com.rictacius.punishSystem.menus;

import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.inventory.meta.SkullMeta;

import com.rictacius.punishSystem.Main;
import com.rictacius.punishSystem.utils.History;
import com.rictacius.punishSystem.utils.PermCheck;
import com.rictacius.punishSystem.utils.ValidItem;

import de.tr7zw.itemnbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;

public class MainMenu implements Listener {
	private static History history = Main.history;

	public static Inventory getMainMenu(Player player, OfflinePlayer toPunish) {
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "PunishSystem | Punish A Player");
		NBTItem nbt = null;
		ItemStack headItem = new ItemStack(Material.SKULL_ITEM);
		SkullMeta headMeta = (SkullMeta) headItem.getItemMeta();
		headMeta.setOwner(toPunish.getName());
		headItem.setItemMeta(headMeta);
		headMeta = (SkullMeta) headItem.getItemMeta();
		ItemMeta tempMeta = null;
		List<String> tempLore = new ArrayList<String>();
		headMeta.setDisplayName(
				ChatColor.translateAlternateColorCodes('&', Main.chat.getPlayerPrefix("world", toPunish))
						+ toPunish.getName());
		tempLore.add(ChatColor.RED + "Rank: " + ChatColor.YELLOW + Main.chat.getPrimaryGroup("world", toPunish));
		tempLore.add("");
		tempLore.add(ChatColor.RED + "Warns: " + ChatColor.YELLOW + "" + history.getWarnCount(toPunish) + "/"
				+ history.getMaxWarns());
		tempLore.add(ChatColor.RED + "Bans: " + ChatColor.YELLOW + "" + history.getBanCount(toPunish));
		tempLore.add("");
		boolean immune = history.isImmune(toPunish);
		if (immune) {
			tempLore.add(ChatColor.RED + "Immune: " + ChatColor.YELLOW + "Yes");
		} else {
			tempLore.add(ChatColor.RED + "Immune: " + ChatColor.YELLOW + "No");
		}
		tempLore.add("");
		headMeta.setLore(tempLore);
		headItem.setItemMeta(headMeta);
		inv.setItem(10, headItem);
		if (PermCheck.hasPerm(player, "punishsystem.history")) {
			ItemStack historyItem = new ItemStack(Material.INK_SACK);
			historyItem.setDurability((short) 2);
			tempMeta = historyItem.getItemMeta();
			tempMeta.setDisplayName(ChatColor.GREEN + "View History");
			historyItem.setItemMeta(tempMeta);
			nbt = new NBTItem(historyItem);
			nbt.setString("menu", "history£" + toPunish.getUniqueId());
			historyItem = nbt.getItem();
			inv.setItem(12, historyItem);
		}
		if (PermCheck.hasPerm(player, "punishsystem.warn")) {
			ItemStack warnItem = new ItemStack(Material.INK_SACK);
			warnItem.setDurability((short) 14);
			tempMeta = warnItem.getItemMeta();
			tempMeta.setDisplayName(ChatColor.GREEN + "Warn Player");
			warnItem.setItemMeta(tempMeta);
			nbt = new NBTItem(warnItem);
			nbt.setString("menu", "warn£" + toPunish.getUniqueId());
			warnItem = nbt.getItem();
			inv.setItem(14, warnItem);
		}
		if (PermCheck.hasPerm(player, "punishsystem.ban")) {
			ItemStack banItem = new ItemStack(Material.INK_SACK);
			banItem.setDurability((short) 1);
			tempMeta = banItem.getItemMeta();
			tempMeta.setDisplayName(ChatColor.GREEN + "Ban Player");
			banItem.setItemMeta(tempMeta);
			nbt = new NBTItem(banItem);
			nbt.setString("menu", "ban£" + toPunish.getUniqueId());
			banItem = nbt.getItem();
			inv.setItem(16, banItem);
		}
		return inv;
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Inventory inv = event.getClickedInventory();
		if (ValidItem.invNameStatsWith(inv, ChatColor.GOLD + "PunishSystem | Punish A Player")) {
			event.setCancelled(true);
			Player p = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();
			NBTItem nbt = new NBTItem(item);
			if (nbt.getString("menu") != null) {
				if (nbt.getString("menu").startsWith("history")) {
					String text = nbt.getString("menu");
					UUID uid = UUID.fromString(text.split("£")[1]);
					p.openInventory(HistoryMenu.getHistoryMenu(p, Bukkit.getOfflinePlayer(uid)));
				} else if (nbt.getString("menu").startsWith("warn")) {
					String text = nbt.getString("menu");
					UUID uid = UUID.fromString(text.split("£")[1]);
					GUIWarnInput.requestWarnInput(p, Bukkit.getOfflinePlayer(uid));
				} else if (nbt.getString("menu").startsWith("ban")) {
					String text = nbt.getString("menu");
					UUID uid = UUID.fromString(text.split("£")[1]);
					GUIBanInput.requestBanInput(p, Bukkit.getOfflinePlayer(uid));
				}
			}
		}
	}
}
