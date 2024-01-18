package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SetDescriptionCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final String badSender;
    private final String noInteger;
    private final String badHomeNumber;
    private final String setNewDescription;
    private final String noPermission;

    public SetDescriptionCommand(ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.configManager = configManager;
        this.badSender = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-sender"));
        this.noInteger = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-no-integer"));
        this.badHomeNumber = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-home-number"));
        this.setNewDescription = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("new-description"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("myhome.setdescription")) {
                FileConfiguration homesConfig = configManager.getHomesConfig();

                if (args.length < 3) {
                    player.sendMessage("/home setdescription <home_number> <new_description>");
                }

                String newDescription = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                int homeNumber;
                try {
                    homeNumber = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(noInteger.replace("{player}", player.getName()));
                    return true;
                }

                String playerUUID = player.getUniqueId().toString();

                if (homesConfig.contains("homes" + "." + playerUUID + "." + homeNumber)) {
                    homesConfig.set("homes" + "." + playerUUID + "." + homeNumber + ".description", newDescription);

                    configManager.saveHomesConfig();
                    player.sendMessage(setNewDescription.replace("{player}", player.getName()).replace("{homeNumber}", String.valueOf(homeNumber)).replace("{description}", newDescription));
                } else {
                    player.sendMessage(badHomeNumber.replace("{player}", player.getName()));
                }
            } else {
                player.sendMessage(noPermission.replace("{player}", player.getName()));
            }
        } else {
            sender.sendMessage(badSender);
        }
        return true;
    }
}
