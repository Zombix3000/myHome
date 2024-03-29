package me.zombix.myhome;

import me.zombix.myhome.Config.Updates;
import me.zombix.myhome.commands.*;
import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import me.zombix.myhome.Config.CommandsTabCompleter;

public class MyHome extends JavaPlugin {

    private ConfigManager configManager;
    private Updates updates;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.setupConfig();

        getLogger().info("Plugin myHome has been enabled!");

        registerCommands();

        if (configManager.getMainConfig().getBoolean("check-for-updates")) {
            getLogger().info("Checking for updates...");
            checkForUpdates();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin myHome has been disabled!");
    }

    private void registerCommands() {
        CommandExecutor setHomeCommand = new SetHomeCommand(configManager);
        CommandExecutor homeCommand = new HomeCommand(this, configManager);
        CommandExecutor reloadCommand = new ReloadCommand(configManager);
        CommandExecutor myHomeCommand = new MyHomeCommand(this, configManager, updates);
        CommandExecutor homesCommand = new HomesCommand(configManager);
        TabCompleter commandsTabCompleter = new CommandsTabCompleter();

        getCommand("sethome").setExecutor(setHomeCommand);
        getCommand("home").setExecutor(homeCommand);
        getCommand("reload").setExecutor(reloadCommand);
        getCommand("myhome").setExecutor(myHomeCommand);
        getCommand("myhome").setTabCompleter(commandsTabCompleter);
        getCommand("homes").setExecutor(homesCommand);
        getCommand("homes").setTabCompleter(commandsTabCompleter);
    }

    private void checkForUpdates() {
        String pluginName = "myHome";
        String currentVersion = "v" + getDescription().getVersion();
        String owner = "Zombix3000";
        String repository = "myHome";

        Updates updates = new Updates(pluginName, currentVersion, owner, repository, this);

        if (updates.checkForUpdates()) {
            getLogger().warning("A new version of the plugin is available! (Current: " + getDescription().getVersion() + ", Latest: " + updates.getLatestVersion() + ")");
        } else {
            getLogger().info("The current version of the plugin is the latest.");
        }
    }

}
