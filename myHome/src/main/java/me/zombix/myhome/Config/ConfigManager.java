package me.zombix.myhome.Config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration homesConfig;
    private FileConfiguration mainConfig;
    private FileConfiguration messagesConfig;
    private FileConfiguration permissionsConfig;

    private final File homesFile;
    private final File configFile;
    private final File messagesFile;
    private File permissionsFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.permissionsFile = new File(plugin.getDataFolder(), "permissions.yml");
    }

    public void setupConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        if (!homesFile.exists()) {
            plugin.saveResource("homes.yml", false);
        }
        if (!permissionsFile.exists()) {
            plugin.saveResource("permissions.yml", false);
        }

        homesConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "homes.yml"));
        permissionsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "permissions.yml"));
        mainConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        messagesConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
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

    public void savePermissionsConfig() {
        try {
            permissionsConfig.save(permissionsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save permissions.yml!");
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

    public FileConfiguration getPermissionsConfig() {
        return permissionsConfig;
    }

    public int getHomesLimitForPermission(Player player) {
        List<Integer> homesLimits = new ArrayList<>();

        homesLimits.add(mainConfig.getInt("default-homes"));
        for (String key : permissionsConfig.getKeys(true)) {
            if (key.endsWith(".homes")) {
                int lastDotIndex = key.lastIndexOf(".");
                String parentKey = key.substring(0, lastDotIndex);

                if (player.hasPermission(parentKey)) {
                    int homesLimit = permissionsConfig.getInt(key);
                    homesLimits.add(homesLimit);
                }
            }
        }

        return Collections.max(homesLimits);
    }
}