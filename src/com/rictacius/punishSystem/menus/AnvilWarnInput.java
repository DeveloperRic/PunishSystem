package com.rictacius.punishSystem.menus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rictacius.punishSystem.utils.ConfigParser;
import com.rictacius.punishSystem.utils.ValidItem;

public class AnvilWarnInput implements Listener {
	private static HashMap<UUID, UUID> waiting = new HashMap<UUID, UUID>();

	public static void requestWarnInput(final Player sender, OfflinePlayer target) {
		sender.closeInventory();
		waiting.put(sender.getUniqueId(), target.getUniqueId());
		Inventory inv = Bukkit.createInventory(sender, InventoryType.ANVIL, "Input reason to WARN " + target.getName());
		ItemStack item = new ItemStack(Material.NAME_TAG);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Rename this NameTag");
		if (ConfigParser.sourceTargetParse("requests.warninput-message", sender.getName(), target).length() > 40) {
			im.setLore(
					Arrays.asList(ConfigParser.sourceTargetParse("requests.warninput-message", sender.getName(), target)
							.substring(0, 40), ChatColor.GOLD + "When done take it out from the result."));
		} else {
			im.setLore(Arrays.asList(
					ConfigParser.sourceTargetParse("requests.warninput-message", sender.getName(), target),
					ChatColor.GOLD + "When done take it out from the result."));
		}
		item.setItemMeta(im);
		sender.getInventory().remove(item);
		sender.getInventory().addItem(item);
		sender.openInventory(inv);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player p = (Player) event.getPlayer();
		try {
			Inventory inv = event.getInventory();
			if (ValidItem.invNameStatsWith(inv, "Input reason to WARN ")) {
				ItemStack item = event.getInventory().getItem(0);
				if (ValidItem.nameIs(item, ChatColor.GREEN + "Rename this NameTag")) {
					if (waiting.containsKey(p.getUniqueId())) {
						OfflinePlayer target = Bukkit.getOfflinePlayer(waiting.get(p.getUniqueId()));
						p.sendMessage(ConfigParser.sourceTargetParse("requests.canceled-message", p.getName(), target));
						p.openInventory(MainMenu.getMainMenu(p, target));
						return;
					}
				}
			}
		} catch (Exception e) {
			if (waiting.containsKey(p.getUniqueId())) {
				OfflinePlayer target = Bukkit.getOfflinePlayer(waiting.get(p.getUniqueId()));
				p.sendMessage(ChatColor.RED + "Could not get your input, using old method!");
				GUIWarnInput.requestWarnInput(p, target);
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		try {
			Inventory inv = event.getInventory();
			if (ValidItem.invNameStatsWith(inv, "Input reason to WARN ")) {
				ItemStack item = event.getCurrentItem();
				if (event.getRawSlot() != 2) {
					return;
				}
				if (item.getType() != Material.NAME_TAG) {
					return;
				}
				if (!ValidItem.nameIs(item, ChatColor.GREEN + "Rename this NameTag")) {
					String name = ValidItem.getName(item);
					if (!name.equals("")) {
						event.setCancelled(true);
						if (waiting.containsKey(p.getUniqueId())) {
							String reason = name.replaceAll("£", "");
							OfflinePlayer target = Bukkit.getOfflinePlayer(waiting.get(p.getUniqueId()));
							GUIWarnInput.doTask(target, reason, p.getUniqueId().toString());
						}
					}
				}
			}
		} catch (Exception e) {
			if (waiting.containsKey(p.getUniqueId())) {
				OfflinePlayer target = Bukkit.getOfflinePlayer(waiting.get(p.getUniqueId()));
				p.sendMessage(ChatColor.RED + "Could not get your input, using old method!");
				p.closeInventory();
				GUIWarnInput.requestWarnInput(p, target);
			}
		}
	}
}
