package com.rictacius.punishSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.rictacius.punishSystem.commands.BanCommand;
import com.rictacius.punishSystem.commands.ModifyCommand;
import com.rictacius.punishSystem.commands.PunishCommand;
import com.rictacius.punishSystem.commands.WarnCommand;
import com.rictacius.punishSystem.menus.GUIBanInput;
import com.rictacius.punishSystem.menus.GUIWarnInput;
import com.rictacius.punishSystem.menus.HistoryMenu;
import com.rictacius.punishSystem.menus.MainMenu;
import com.rictacius.punishSystem.utils.History;
import com.rictacius.punishSystem.utils.ServerChecker;
import com.rictacius.punishSystem.utils.TempBans;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin implements Listener {
	PluginDescriptionFile pdfFile = getDescription();
	Logger logger = getLogger();

	public static Main pl;

	public void onEnable() {
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Registering Config...."), ChatColor.YELLOW);
		createFiles();
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Registering Utils...."), ChatColor.YELLOW);
		registerUtils();
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Registering Commands...."), ChatColor.YELLOW);
		registerCommands();
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Registering Events...."), ChatColor.YELLOW);
		registerEvents();
		Methods.sendColoredMessage(this, ChatColor.AQUA,
				(pdfFile.getName() + " has been enabled! (V." + pdfFile.getVersion() + ")"), ChatColor.GREEN);
	}

	public static Chat chat = null;
	public static Permission permission = null;
	public static History history = null;
	public static TempBans tempbans = null;

	public void registerUtils() {
		pl = this;
		setupChat();
		setupPermissions();
		history = new History();
		tempbans = new TempBans();
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	public void onDisable() {

		Methods.sendColoredMessage(this, ChatColor.AQUA,
				(pdfFile.getName() + " has been disabled! (V." + pdfFile.getVersion() + ")"), ChatColor.YELLOW);
	}

	public void registerCommands() {
		try {
			getCommand("punish").setExecutor(new PunishCommand());
			getCommand("warn").setExecutor(new WarnCommand());
			getCommand("ban").setExecutor(new BanCommand());
			getCommand("psmodify").setExecutor(new ModifyCommand());
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Error while registering commands!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Commands successfuly registered!"), ChatColor.LIGHT_PURPLE);
	}

	public void registerEvents() {
		try {
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(new MainMenu(), this);
			pm.registerEvents(new GUIWarnInput(), this);
			pm.registerEvents(new GUIBanInput(), this);
			pm.registerEvents(new HistoryMenu(), this);
			pm.registerEvents(new ServerChecker(), this);
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Error while registering events!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.AQUA, ("Trace:"), ChatColor.RED);
			e.printStackTrace();
		}
		Methods.sendColoredMessage(this, ChatColor.AQUA, ("Events successfuly registered!"), ChatColor.LIGHT_PURPLE);
	}

	public void registerExternalEvents(Listener eventClass) {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(eventClass, this);
		Methods.sendColoredMessage(this, ChatColor.AQUA,
				("Registered External Events in Listener " + eventClass.getClass().getSimpleName()),
				ChatColor.LIGHT_PURPLE);
	}

	public static Plugin getPlugin() {
		return Bukkit.getServer().getPluginManager().getPlugin("PunishSystem");
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private File configf, defaultf, tempbansf;
	private FileConfiguration config, defaultc, tempbansc;

	public FileConfiguration getDefaultDataConfig() {
		return this.defaultc;
	}

	public FileConfiguration getTempbansConfig() {
		return this.tempbansc;
	}

	public int reloadAllConfigFiles() {
		int errors = 0;
		ArrayList<String> errorFiles = new ArrayList<String>();
		String file = "";
		ArrayList<StackTraceElement[]> traces = new ArrayList<StackTraceElement[]>();
		StackTraceElement[] trace = null;
		try {
			this.reloadConfig();
		} catch (Exception e) {
			errors++;
			trace = e.getStackTrace();
			traces.add(trace);
			file = "Main Config File";
			errorFiles.add(file);
		}
		try {
			defaultc = YamlConfiguration.loadConfiguration(defaultf);
		} catch (Exception e) {
			errors++;
			trace = e.getStackTrace();
			traces.add(trace);
			file = "Default Data Config File";
			errorFiles.add(file);
		}
		try {
			tempbansc = YamlConfiguration.loadConfiguration(tempbansf);
		} catch (Exception e) {
			errors++;
			trace = e.getStackTrace();
			traces.add(trace);
			file = "Tempban Config File";
			errorFiles.add(file);
		}
		if (errors > 0) {
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Could not reload all config files!"), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("The following files generated erros:"), ChatColor.RED);
			for (String fileName : errorFiles) {
				Methods.sendColoredMessage(this, ChatColor.GOLD, (ChatColor.GRAY + " - " + ChatColor.RED + fileName),
						ChatColor.RED);
			}
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Trace(s):"), ChatColor.RED);
			for (StackTraceElement[] currentTrace : traces) {
				int i = 0;
				Methods.sendColoredMessage(this, ChatColor.GOLD,
						(ChatColor.GRAY + "* " + ChatColor.RED + errorFiles.get(i)), ChatColor.RED);
				for (StackTraceElement printTrace : currentTrace) {
					Methods.sendColoredMessage(this, ChatColor.GOLD, (printTrace.toString()), ChatColor.RED);
				}
				i++;
			}
		}
		return errors;
	}

	public void reloadTempBans() {
		try {
			tempbansc.load(tempbansf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveAllConfigFiles() {
		try {
			saveConfig();
		} catch (Exception ex) {
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Could not save config to " + configf), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Trace:"), ChatColor.RED);
			ex.printStackTrace();
		}
		try {
			getDefaultDataConfig().save(defaultf);
		} catch (Exception ex) {
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Could not save config to " + defaultf), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Trace:"), ChatColor.RED);
			ex.printStackTrace();
		}
		try {
			getTempbansConfig().save(tempbansf);
		} catch (Exception ex) {
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Could not save config to " + tempbansf), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Trace:"), ChatColor.RED);
			ex.printStackTrace();
		}
	}

	public void saveDefaultDataFile() {
		try {
			getDefaultDataConfig().save(defaultf);
		} catch (Exception ex) {
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Could not save config to " + defaultf), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Trace:"), ChatColor.RED);
			ex.printStackTrace();
		}
	}

	public void saveTempBansFile() {
		try {
			getTempbansConfig().save(tempbansf);
		} catch (Exception ex) {
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Could not save config to " + tempbansf), ChatColor.RED);
			Methods.sendColoredMessage(this, ChatColor.GOLD, ("Trace:"), ChatColor.RED);
			ex.printStackTrace();
		}
	}

	private void createFiles() {
		try {
			configf = new File(getDataFolder(), "config.yml");
			defaultf = new File(getDataFolder(), "default_data.yml");
			tempbansf = new File(getDataFolder(), "tempbans.yml");

			if (!configf.exists()) {
				configf.getParentFile().mkdirs();
				saveResource("config.yml", false);
			}
			if (!defaultf.exists()) {
				defaultf.getParentFile().mkdirs();
				saveResource("default_data.yml", false);
			}
			if (!tempbansf.exists()) {
				tempbansf.getParentFile().mkdirs();
				saveResource("tempbans.yml", false);
			}

			config = new YamlConfiguration();
			defaultc = new YamlConfiguration();
			tempbansc = new YamlConfiguration();
			try {
				config.load(configf);
				defaultc.load(defaultf);
				tempbansc.load(tempbansf);
			} catch (Exception e) {
				Methods.sendColoredMessage(this, ChatColor.LIGHT_PURPLE, ("Error while registering config!"),
						ChatColor.RED);
				e.printStackTrace();
			}
			getConfig().options().copyDefaults(true);
			getDefaultDataConfig().options().copyDefaults(true);
			getTempbansConfig().options().copyDefaults(true);
			saveAllConfigFiles();
		} catch (Exception e) {
			Methods.sendColoredMessage(this, ChatColor.LIGHT_PURPLE, ("Error while registering config!"),
					ChatColor.RED);
			e.printStackTrace();
		}
	}
}