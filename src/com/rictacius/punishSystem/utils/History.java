package com.rictacius.punishSystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.rictacius.punishSystem.Main;

import net.md_5.bungee.api.ChatColor;

public class History {
	private Main plugin = Main.pl;
	private ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static String prefix;
	private int maxWarns;

	public History() {
		reloadHistory();
	}

	public void reloadHistory() {
		setMaxWarns(Integer.parseInt(plugin.getConfig().getString("max-warns")));
		prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("message-prefix"));
	}

	public FileConfiguration getData(OfflinePlayer p) {
		File file = new File(plugin.getDataFolder() + "/history/" + p.getUniqueId() + ".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				plugin.getDefaultDataConfig().save(file);
			} catch (IOException e) {
				console.sendMessage(prefix + ChatColor.RED + "Error: Could not create player file for " + p.getName());
				e.printStackTrace();
			}
		}
		FileConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (FileNotFoundException e) {
			console.sendMessage(prefix + ChatColor.RED + "Error: Could not find player file for " + p.getName());
			e.printStackTrace();
		} catch (IOException e) {
			console.sendMessage(prefix + ChatColor.RED + "Error: Could not import player file for " + p.getName());
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			console.sendMessage(prefix + ChatColor.RED + "Error: Player file for " + p.getName() + " is invalid!");
			e.printStackTrace();
		}
		config.options().copyDefaults(true);
		config.set("name", p.getName());
		if (PermCheck.hasAccess(p, "punishsystem.alwaysimmune")) {
			config.set("immune", true);
		}
		try {
			config.save(file);
		} catch (IOException e) {
			console.sendMessage(prefix + ChatColor.RED + "Error: Could not update player file for " + p.getName());
			e.printStackTrace();
		}
		return config;
	}

	public File getFile(OfflinePlayer p) {
		File file = new File(plugin.getDataFolder() + "/history/" + p.getUniqueId() + ".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				plugin.getDefaultDataConfig().save(file);
			} catch (IOException e) {
				console.sendMessage(prefix + ChatColor.RED + "Error: Could not create player file for " + p.getName());
				e.printStackTrace();
			}
		}
		return file;
	}

	public void saveData(FileConfiguration data, OfflinePlayer p) {
		File file = new File(plugin.getDataFolder() + "/history/" + p.getUniqueId() + ".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				plugin.getDefaultDataConfig().save(file);
			} catch (IOException e) {
				console.sendMessage(prefix + ChatColor.RED + "Error: Could not create player file for " + p.getName());
				e.printStackTrace();
			}
		}
		try {
			data.save(file);
		} catch (IOException e) {
			console.sendMessage(prefix + ChatColor.RED + "Error: Could not update player file for " + p.getName());
			e.printStackTrace();
		}
	}

	public int getWarnCount(OfflinePlayer p) {
		FileConfiguration config = getData(p);
		int warns = config.getConfigurationSection("warns").getKeys(false).size();
		return warns;
	}

	public int getBanCount(OfflinePlayer p) {
		FileConfiguration config = getData(p);
		int bans = config.getConfigurationSection("bans").getKeys(false).size();
		return bans;
	}

	public int getHistoryCount(OfflinePlayer p) {
		FileConfiguration config = getData(p);
		int warns = config.getConfigurationSection("history").getKeys(false).size();
		return warns;
	}

	public Set<String> getHistory(OfflinePlayer p) {
		FileConfiguration config = getData(p);
		return config.getConfigurationSection("history").getKeys(false);
	}

	public String getHistoryInfo(OfflinePlayer p, String label) {
		FileConfiguration config = getData(p);
		String record = config.getString("history." + label);
		try {
			record = ProtectedConfigFile.decrypt(record);
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
		return record;
	}

	public boolean isImmune(OfflinePlayer p) {
		FileConfiguration config = getData(p);
		boolean immune = Boolean.parseBoolean(config.getString("immune"));
		return immune;
	}

	public int getMaxWarns() {
		return maxWarns;
	}

	public void setMaxWarns(int maxWarns) {
		this.maxWarns = maxWarns;
	}

	public void warnPlayer(OfflinePlayer target, String reason, String source) {
		FileConfiguration config = getData(target);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		String strdate = dateFormat.format(date);
		String output = "warn£" + source + "£" + strdate + "£" + reason;
		int warns = getWarnCount(target);
		int hiscount = getHistoryCount(target);
		try {
			config.set("warns." + String.valueOf(warns + 1), ProtectedConfigFile.encrypt(output));
			config.set("history." + String.valueOf(hiscount + 1), ProtectedConfigFile.encrypt(output));
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveData(config, target);
	}

	public Date banPlayer(OfflinePlayer target, String reason, String source, int time) {
		FileConfiguration config = getData(target);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		String strdate = dateFormat.format(date);
		String output = "ban£" + source + "£" + strdate + "£" + reason + "£" + time;
		int bans = getBanCount(target);
		int hiscount = getHistoryCount(target);
		try {
			config.set("bans." + String.valueOf(bans + 1), ProtectedConfigFile.encrypt(output));
			config.set("history." + String.valueOf(hiscount + 1), ProtectedConfigFile.encrypt(output));
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveData(config, target);
		if (time > 0) {
			int warns = getWarnCount(target);
			try {
				config.set("warns." + String.valueOf(warns + 1), ProtectedConfigFile.encrypt(output));
			} catch (Exception e) {
				e.printStackTrace();
			}
			saveData(config, target);
			return new Date(time * 1000L);
		} else {
			return null;
		}
	}
	
	public Date ipBanPlayer(OfflinePlayer target, String reason, String source, int time) {
		FileConfiguration config = getData(target);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		String strdate = dateFormat.format(date);
		String output = "ipban£" + source + "£" + strdate + "£" + reason + "£" + time;
		int bans = getBanCount(target);
		int hiscount = getHistoryCount(target);
		try {
			config.set("bans." + String.valueOf(bans + 1), ProtectedConfigFile.encrypt(output));
			config.set("history." + String.valueOf(hiscount + 1), ProtectedConfigFile.encrypt(output));
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveData(config, target);
		if (time > 0) {
			int warns = getWarnCount(target);
			try {
				config.set("warns." + String.valueOf(warns + 1), ProtectedConfigFile.encrypt(output));
			} catch (Exception e) {
				e.printStackTrace();
			}
			saveData(config, target);
			return new Date(time * 1000L);
		} else {
			return null;
		}
	}
}
