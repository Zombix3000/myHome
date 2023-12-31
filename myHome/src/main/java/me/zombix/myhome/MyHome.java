package me.zombix.myhome;

import me.zombix.myhome.commands.HomeCommand;
import me.zombix.myhome.commands.SetHomeCommand;
import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class MyHome extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.setupConfig();

        getLogger().info("myHome has been enabled!");

        registerCommands();
    }

    @Override
    public void onDisable() {
        configManager.saveHomesConfig();
        getLogger().info("myHome has been disabled!");
    }

    private void registerCommands() {
        CommandExecutor setHomeCommand = new SetHomeCommand(configManager);
        CommandExecutor homeCommand = new HomeCommand(this, configManager);

        getCommand("sethome").setExecutor(setHomeCommand);
        getCommand("home").setExecutor(homeCommand);
    }

}
