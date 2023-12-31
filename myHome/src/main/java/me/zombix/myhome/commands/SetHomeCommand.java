package me.zombix.myhome.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.zombix.myhome.Config.ConfigManager;

import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class SetHomeCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final String setHomeMessage;
    private final String noPermission;
    private final String badWorld;

    public SetHomeCommand(ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.configManager = configManager;
        this.setHomeMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("set-home"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
        this.badWorld = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-world"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("myhome.sethome")) {
                FileConfiguration mainConfig = configManager.getMainConfig();
                List<String> allowedWorlds = mainConfig.getStringList("allow-worlds");
                getLogger().info(allowedWorlds.toString());

                if (allowedWorlds.contains(player.getWorld().getName())) {
                    FileConfiguration homesConfig = configManager.getHomesConfig();

                    homesConfig.set(player.getUniqueId().toString() + ".x", player.getLocation().getX());
                    homesConfig.set(player.getUniqueId().toString() + ".y", player.getLocation().getY());
                    homesConfig.set(player.getUniqueId().toString() + ".z", player.getLocation().getZ());
                    homesConfig.set(player.getUniqueId().toString() + ".world", player.getLocation().getWorld().getName());
                    if (mainConfig.getBoolean("save-look")) {
                        homesConfig.set(player.getUniqueId().toString() + ".yaw", player.getLocation().getYaw());
                        homesConfig.set(player.getUniqueId().toString() + ".pitch", player.getLocation().getPitch());
                    }

                    configManager.saveHomesConfig();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', setHomeMessage.replace("{player}", player.getName())));
                    return true;
                } else {
                    player.sendMessage(badWorld.replace("{player}", player.getName()));
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermission.replace("{player}", player.getName())));
                return false;
            }
        } else {
            sender.sendMessage("This command can only be used by a player!");
            return true;
        }
    }
}
