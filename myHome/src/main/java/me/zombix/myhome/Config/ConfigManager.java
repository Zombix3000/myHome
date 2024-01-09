package me.zombix.myhome.Config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration homesConfig;
    private FileConfiguration mainConfig;
    private FileConfiguration messagesConfig;
    private final File homesFile;
    private final File configFile;
    private final File messagesFile;
    private File permissionsFile;
    private FileConfiguration permissionsConfig;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.permissionsFile = new File(plugin.getDataFolder(), "permissions.yml");
        this.permissionsConfig = new YamlConfiguration();
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

        if (!permissionsFile.exists()) {
            plugin.saveResource("permissions.yml", false);
        }

        permissionsConfig = YamlConfiguration.loadConfiguration(permissionsFile);
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        mainConfig = YamlConfiguration.loadConfiguration(configFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void createMainConfig() {
        List<String> allowedWorlds = Arrays.asList("world", "world_nether", "world_end");

        mainConfig.set("delay-require", true);
        mainConfig.set("delay", 5);
        mainConfig.set("allow-movement", false);
        mainConfig.set("allow-worlds", allowedWorlds);
        mainConfig.set("save-look", true);
        mainConfig.set("default-homes", 1);
        mainConfig.set("auto-overwriting-homes", true);
        mainConfig.set("default-overwriting-home", 1);
        mainConfig.set("default-tp-home", 1);
        //mainConfig.set("ask-sure-overwrite", true);
        try {
            mainConfig.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not create config.yml!");
        }
    }

    private void createMessagesConfig() {
        messagesConfig.set("set-home-without-ds", "&aYou set your &b{homeNumber} &ahome, &6{player}&a!");
        messagesConfig.set("set-home-with-ds", "&aYou set your &b{homeNumber} &ahome with description: &b{description}&a, &6{player}&a!");
        messagesConfig.set("no-home", "&cYou don't set a home yet!");
        messagesConfig.set("home-tp", "&aSuccessfully teleported to home!");
        messagesConfig.set("is-delay", "&7Teleporting in {delay} seconds...");
        messagesConfig.set("is-moving", "&cTeleportation canceled because you was moved when you waiting for teleportation!");
        messagesConfig.set("no-permission", "&cYou don't have permission to use this command!");
        messagesConfig.set("successfully-reloaded", "&aPlugin myHome has been reloaded!");
        messagesConfig.set("bad-world", "&cYou cannot set home in this world!");
        messagesConfig.set("home-no-integer", "&cHome number must be integer!");
        messagesConfig.set("home-limit", "&cHomes limit is &b{homesLimit}&c!");
        messagesConfig.set("all-homes-busy", "&cYou already save locations in all homes!");
        messagesConfig.set("failed-load-configuration", "&cFailed to load yml configuration! Check what wrong and try again.");
        messagesConfig.set("header-homes-list", "&aYour homes:");
        messagesConfig.set("bad-sender", "&cThis command can only be used by a player!");
        messagesConfig.set("bad-home-number", "&cYou don't have a home with this number!");
        messagesConfig.set("new-description", "&aSuccessfully set new description for &b{homeNumber} &ahome!");
        messagesConfig.set("home-deleted", "&aSuccessfully deleted your &b{homeNumber} &ahome!");
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

    public FileConfiguration getPermissionsConfig() { return permissionsConfig; }

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