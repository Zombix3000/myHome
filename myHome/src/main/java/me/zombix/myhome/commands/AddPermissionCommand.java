package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class AddPermissionCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final String noPermission;
    private final String noInteger;
    private final String successfullyAddPermission;

    public AddPermissionCommand(ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.configManager = configManager;
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
        this.noInteger = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-no-integer"));
        this.successfullyAddPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("added-permission"));
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

                permissionsConfig.set(permission + "." + "homes", homesNumber);

                configManager.savePermissionsConfig();

                sender.sendMessage(successfullyAddPermission.replace("{permission}", permission).replace("{homesNumber}", String.valueOf(homesNumber)));
            } else {
                sender.sendMessage(noPermission);
            }
        return true;
    }
}
