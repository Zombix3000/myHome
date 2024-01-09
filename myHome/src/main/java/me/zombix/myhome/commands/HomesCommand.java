package me.zombix.myhome.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.zombix.myhome.Config.ConfigManager;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HomesCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final String noHomes;
    private final String headerHomesList;
    private final String badSender;
    private final String noPermission;

    public HomesCommand(ConfigManager configManager) {
        this.configManager = configManager;

        FileConfiguration messagesConfig = configManager.getMessagesConfig();
        this.noHomes = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-home"));
        this.headerHomesList = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("header-homes-list"));
        this.badSender = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-sender"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("myhome.homes")) {
                FileConfiguration homesConfig = configManager.getHomesConfig();
                String playerUUID = player.getUniqueId().toString();

                if (homesConfig.contains(playerUUID)) {
                    ConfigurationSection playerHomes = homesConfig.getConfigurationSection(playerUUID);
                    Set<String> homes = playerHomes.getKeys(false);
                    List<Integer> homeNumbers = homes.stream()
                            .mapToInt(Integer::parseInt)
                            .boxed()
                            .sorted()
                            .collect(Collectors.toList());

                    if (homes != null && !homes.isEmpty()) {
                        StringBuilder homesList = new StringBuilder();
                        homesList.append(headerHomesList);

                        for (int i = 1; i <= homeNumbers.size(); i++) {
                            Integer homeNumber = homeNumbers.get(i - 1);
                            String description = homesConfig.getString(playerUUID + "." + homeNumber + ".description", "");
                            homesList.append("\n&7- ").append("&b").append(homeNumber);
                            if (!description.isEmpty()) {
                                homesList.append("&7, ").append("&e").append(description);
                            }
                        }

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', homesList.toString()));
                    } else {
                        player.sendMessage(noHomes);
                    }
                } else {
                    player.sendMessage(noHomes);
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
