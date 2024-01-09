package me.zombix.myhome;

import me.zombix.myhome.commands.*;
import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import me.zombix.myhome.Config.CommandsTabCompleter;

public class MyHome extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.setupConfig();

        getLogger().info("Plugin myHome has been enabled!");

        registerCommands();
    }

    @Override
    public void onDisable() {
        configManager.saveHomesConfig();
        getLogger().info("Plugin myHome has been disabled!");
    }

    private void registerCommands() {
        CommandExecutor setHomeCommand = new SetHomeCommand(configManager);
        CommandExecutor homeCommand = new HomeCommand(this, configManager);
        CommandExecutor reloadCommand = new ReloadCommand(configManager);
        CommandExecutor myHomeCommand = new MyHomeCommand(this, configManager);
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

}
