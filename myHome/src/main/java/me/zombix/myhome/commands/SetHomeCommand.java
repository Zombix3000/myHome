package me.zombix.myhome.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import me.zombix.myhome.Config.ConfigManager;

import java.util.Arrays;
import java.util.List;

public class SetHomeCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final String setHomeMessageWithoutDs;
    private final String setHomeMessageWithDs;
    private final String noPermission;
    private final String badWorld;
    private final String noInteger;
    private final String homeLimit;
    private final String allHomesBusy;
    private final String wrongConfiguration;
    private final String badSender;

    public SetHomeCommand(ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.configManager = configManager;
        this.setHomeMessageWithoutDs = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("set-home-without-ds"));
        this.setHomeMessageWithDs = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("set-home-with-ds"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
        this.badWorld = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-world"));
        this.noInteger = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-no-integer"));
        this.homeLimit = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-limit"));
        this.allHomesBusy = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("all-homes-busy"));
        this.wrongConfiguration = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("failed-load-configuration"));
        this.badSender = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-sender"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("myhome.sethome")) {
                FileConfiguration mainConfig = configManager.getMainConfig();
                List<String> allowedWorlds = mainConfig.getStringList("allow-worlds");
                int homesLimit = configManager.getHomesLimitForPermission(player);

                if (mainConfig.getInt("default-overwriting-home") > mainConfig.getInt("default-homes")) {
                    player.sendMessage(wrongConfiguration.replace("{player}", player.getName()));
                    return true;
                }

                FileConfiguration homesConfig = configManager.getHomesConfig();
                String playerUUID = player.getUniqueId().toString();

                int homeNumber;
                if (args.length < 1) {
                    int homeAvailable = findNextAvailableHome(player, homesLimit);

                    if (homeAvailable == -1) {
                        if (configManager.getMainConfig().getBoolean("auto-overwriting-homes")) {
                            homeNumber = 1;
                        } else {
                            player.sendMessage(allHomesBusy.replace("{player}", player.getName()));
                            return true;
                        }
                    } else {
                        homeNumber = homeAvailable;
                    }
                } else {
                    try {
                        homeNumber = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(noInteger.replace("{player}", player.getName()));
                        return true;
                    }
                }


                if (homeNumber < 1 || homeNumber > homesLimit) {
                    player.sendMessage(homeLimit.replace("{player}", player.getName()).replace("{homesLimit}", String.valueOf(homesLimit)));
                    return true;
                }

                Location homeLocation = player.getLocation();

                if (!allowedWorlds.contains(homeLocation.getWorld().getName())) {
                    player.sendMessage(badWorld.replace("{player}", player.getName()));
                    return true;
                }

                String worldName = homeLocation.getWorld().getName();
                double x = homeLocation.getX();
                double y = homeLocation.getY();
                double z = homeLocation.getZ();

                String description = "";
                if (args.length > 1) {
                    description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                }

                homesConfig.set(playerUUID + "." + homeNumber + ".location.world", worldName);
                homesConfig.set(playerUUID + "." + homeNumber + ".location.x", x);
                homesConfig.set(playerUUID + "." + homeNumber + ".location.y", y);
                homesConfig.set(playerUUID + "." + homeNumber + ".location.z", z);

                if (configManager.getMainConfig().getBoolean("save-look")) {
                    float yaw = homeLocation.getYaw();
                    float pitch = homeLocation.getPitch();

                    homesConfig.set(playerUUID + "." + homeNumber + ".location.yaw", yaw);
                    homesConfig.set(playerUUID + "." + homeNumber + ".location.pitch", pitch);
                }

                homesConfig.set(playerUUID + "." + homeNumber + ".description", description);

                configManager.saveHomesConfig();

                String setHomeMessage;
                if (!description.equals("")) {
                    setHomeMessage = setHomeMessageWithDs.replace("{description}", description);
                } else {
                    setHomeMessage = setHomeMessageWithoutDs;
                }

                player.sendMessage(setHomeMessage.replace("{player}", player.getName()).replace("{homeNumber}", String.valueOf(homeNumber)));
                return true;
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermission.replace("{player}", player.getName())));
                return false;
            }
        } else {
            sender.sendMessage(badSender);
            return true;
        }
    }

    private int findNextAvailableHome(Player player, int homesLimit) {
        FileConfiguration homesConfig = configManager.getHomesConfig();
        String playerUUID = player.getUniqueId().toString();

        for (int i = 1; i <= homesLimit; i++) {
            if (homesConfig.contains(playerUUID + "." + i)) {
                continue;
            } else {
                return i;
            }
        }

        return -1;
    }
}
