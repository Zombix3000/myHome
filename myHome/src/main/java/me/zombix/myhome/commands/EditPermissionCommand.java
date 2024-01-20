package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class EditPermissionCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final String noPermission;
    private final String noInteger;
    private final String successfullyAddPermission;
    private final String isNotPermission;

    public EditPermissionCommand(ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.configManager = configManager;
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
        this.noInteger = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-no-integer"));
        this.successfullyAddPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("added-permission"));
        this.isNotPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("is-not-permission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("myhome.managepermissions")) {
            FileConfiguration permissionsConfig = configManager.getPermissionsConfig();

            int homesNumber;
            String permission;
            if (args.length > 2) {
                try {
                    homesNumber = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(noInteger);
                    return true;
                }
                permission = args[1];
            } else {
                return false;
            }

            if (!permissionsConfig.contains("permissions" + "." + permission)) {
                sender.sendMessage(isNotPermission);
                return true;
            }

            permissionsConfig.set("permissions" + "." + permission + "." + "homes", homesNumber);

            configManager.savePermissionsConfig();

            sender.sendMessage(successfullyAddPermission.replace("{permission}", permission).replace("{homesNumber}", String.valueOf(homesNumber)));
        } else {
            sender.sendMessage(noPermission);
        }
        return true;
    }
}
