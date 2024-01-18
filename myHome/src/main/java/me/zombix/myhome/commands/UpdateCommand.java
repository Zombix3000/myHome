package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import me.zombix.myhome.Config.Updates;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UpdateCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final Updates updates;
    private final String noPermission;

    public UpdateCommand(ConfigManager configManager, Updates updates) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.configManager = configManager;
        this.updates = updates;
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (sender.hasPermission("myhome.update")) {
                if (updates.checkForUpdates()) {
                    updates.updatePlugin();
                    sender.sendMessage("Plugin successfully updated! Please reload the server to apply changes.");
                } else {
                    sender.sendMessage("No updates available.");
                }
            } else {
                sender.sendMessage(noPermission.replace("{player}", player.getName()));
            }
        } else {
            if (updates.checkForUpdates()) {
                updates.updatePlugin();
                sender.sendMessage("Plugin successfully updated! Please reload the server to apply changes.");
            } else {
                sender.sendMessage("No updates available.");
            }
        }
        return true;
    }
}
