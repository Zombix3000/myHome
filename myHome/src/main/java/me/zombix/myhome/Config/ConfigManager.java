package me.zombix.myhome.Config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration homesConfig;
    private FileConfiguration mainConfig;
    private FileConfiguration messagesConfig;
    private final File homesFile;
    private final File configFile;
    private final File messagesFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.mainConfig = new YamlConfiguration();
        this.messagesConfig = new YamlConfiguration();
    }

    public void setupConfig() {
        if (!homesFile.exists()) {
            plugin.saveResource("homes.yml", false);
        }

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            createMainConfig();
        }

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
            createMessagesConfig();
        }

        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        mainConfig = YamlConfiguration.loadConfiguration(configFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void createMainConfig() {
        mainConfig.set("delay-require", true);
        mainConfig.set("delay", 5);
        mainConfig.set("allow-movement", false);
        try {
            mainConfig.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not create config.yml!");
        }
    }

    private void createMessagesConfig() {
        messagesConfig.set("set-home", "&aYou set your new home, &6{player}&a!");
        messagesConfig.set("no-home", "&cYou don't set a home yet!");
        messagesConfig.set("home-tp", "&aSuccessfully teleported to home!");
        messagesConfig.set("is-delay", "&7Teleporting in {delay} seconds...");
        messagesConfig.set("is-moving", "&cTeleportation canceled because you was moved when you waiting for teleportation!");
        messagesConfig.set("no-permission", "&cYou don't have permission to use this command!");
        messagesConfig.set("successfully-reloaded", "&aPlugin myHome has been reloaded!");
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not create messages.yml!");
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void saveHomesConfig() {
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save homes.yml!");
        }
    }

    public FileConfiguration getHomesConfig() {
        return homesConfig;
    }

    public FileConfiguration getMainConfig() {
        return mainConfig;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }
}
