package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DeleteHomeCommand implements CommandExecutor {
    private final ConfigManager configManager;
    private final String badSender;
    private final String noInteger;
    private final String badHomeNumber;
    private final String homeDeleted;
    private final String noPermission;

    public DeleteHomeCommand(ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.configManager = configManager;
        this.badSender = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-sender"));
        this.noInteger = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-no-integer"));
        this.badHomeNumber = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-home-number"));
        this.homeDeleted = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-deleted"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            FileConfiguration homesConfig = configManager.getHomesConfig();
            Player player = (Player) sender;
            String commandString = command.toString();

            if (player.hasPermission("myhome.deletehome")) {
                if (commandString.contains("home") && args.length < 2) {
                    player.sendMessage("/home delete <home_number>");
                    return true;
                } else if (commandString.contains("myhome") && args.length < 3) {
                    player.sendMessage("/myhome home delete <home_number>");
                    return true;
                }
                player.sendMessage(command.toString());
                player.sendMessage(String.valueOf(args.length));

                int homeNumber;
                try {
                    homeNumber = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    if (args.length > 2) {
                        try {
                            homeNumber = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e2) {
                            player.sendMessage(noInteger.replace("{player}", player.getName()));
                            return true;
                        }
                    } else {
                        player.sendMessage(noInteger.replace("{player}", player.getName()));
                        return true;
                    }
                }

                String playerUUID = player.getUniqueId().toString();

                if (homesConfig.contains(playerUUID + "." + homeNumber)) {
                    homesConfig.set(playerUUID + "." + homeNumber, null);

                    configManager.saveHomesConfig();
                    player.sendMessage(homeDeleted.replace("{player}", player.getName()).replace("{homeNumber}", String.valueOf(homeNumber)));
                } else {
                    player.sendMessage(badHomeNumber.replace("{player}", player.getName()));
                }
            } else {
                player.sendMessage(noPermission.replace("{player}", player.getName()));
                return true;
            }
        } else {
            sender.sendMessage(badSender);
        }
        return true;
    }
}
