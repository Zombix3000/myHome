package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final String successfullyReloaded;
    private final String noPermission;

    public ReloadCommand(ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.configManager = configManager;
        this.successfullyReloaded = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("successfully-reloaded"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (sender.hasPermission("myhome.reload")) {
                configManager.setupConfig();

                sender.sendMessage(successfullyReloaded.replace("{player}", player.getName()));
            } else {
                sender.sendMessage(noPermission.replace("{player}", player.getName()));
            }
        } else {
            configManager.setupConfig();
            sender.sendMessage(successfullyReloaded);
        }
        return true;
    }
}
