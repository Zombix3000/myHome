package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class HomeCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final String noHomeMessage;
    private final String homeTPMessage;
    private final String movingMessage;
    private final String noPermission;
    private final String delayMessage;
    BukkitTask movementTask = null;

    public HomeCommand(JavaPlugin plugin, ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.plugin = plugin;
        this.configManager = configManager;
        this.noHomeMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-home"));
        this.homeTPMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-tp"));
        this.movingMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("is-moving"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
        this.delayMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("is-delay"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("myhome.home")) {
                FileConfiguration homesConfig = configManager.getHomesConfig();
                FileConfiguration mainConfig = configManager.getMainConfig();

                if (homesConfig.get(player.getUniqueId().toString()) != null) {
                    if (mainConfig.getBoolean("delay-require")) {
                        int delaySeconds = mainConfig.getInt("delay");
                        String delayStr = String.valueOf(delaySeconds);

                        player.sendMessage(delayMessage.replace("{player}", player.getName()).replace("{delay}", delayStr));

                        if (mainConfig.getBoolean("allow-movement")) {
                            int taskId = Bukkit.getScheduler().runTaskLater(configManager.getPlugin(), () -> {
                                if (player.isOnline() && !player.isDead()) {
                                    teleportHome(player, homesConfig);
                                }
                            }, delaySeconds * 20L).getTaskId();

                            player.setMetadata("teleportTaskID", new org.bukkit.metadata.FixedMetadataValue(configManager.getPlugin(), taskId));
                        } else {
                            double initialX = player.getLocation().getX();
                            double initialY = player.getLocation().getY();
                            double initialZ = player.getLocation().getZ();
                            String initialWorld = player.getLocation().getWorld().getName();

                            try {
                                movementTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                    double currentX = player.getLocation().getX();
                                    double currentY = player.getLocation().getY();
                                    double currentZ = player.getLocation().getZ();
                                    String currentWorld = player.getLocation().getWorld().getName();

                                    if (initialX != currentX || initialY != currentY || initialZ != currentZ || !initialWorld.equals(currentWorld)) {
                                        player.sendMessage(movingMessage.replace("{player}", player.getName()).replace("{delay}", delayStr));
                                        Bukkit.getScheduler().cancelTask(player.getMetadata("teleportTaskID").get(0).asInt());
                                        movementTask.cancel();
                                    }
                                }, 0L, 5L);

                                int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    if (player.isOnline() && !player.isDead()) {
                                        teleportHome(player, homesConfig);
                                        if (movementTask != null) {
                                            movementTask.cancel();
                                        }
                                    }
                                }, delaySeconds * 20L).getTaskId();

                                player.setMetadata("teleportTaskID", new org.bukkit.metadata.FixedMetadataValue(plugin, taskId));
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        teleportHome(player, homesConfig);
                    }
                } else {
                    player.sendMessage(noHomeMessage.replace("{player}", player.getName()));
                }
                return true;
            } else {
                player.sendMessage(noPermission.replace("{player}", player.getName()));
                return false;
            }
        } else {
            sender.sendMessage("This command can only be used by a player!");
            return true;
        }

    }

    private void teleportHome(Player player, FileConfiguration homesConfig) {
        double x = homesConfig.getDouble(player.getUniqueId().toString() + ".x");
        double y = homesConfig.getDouble(player.getUniqueId().toString() + ".y");
        double z = homesConfig.getDouble(player.getUniqueId().toString() + ".z");
        String worldName = homesConfig.getString(player.getUniqueId().toString() + ".world");

        player.teleport(new Location(player.getServer().getWorld(worldName), x, y, z));

        player.sendMessage(homeTPMessage.replace("{player}", player.getName()));
    }
}
