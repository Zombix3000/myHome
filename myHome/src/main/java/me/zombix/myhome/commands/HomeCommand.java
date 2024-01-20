package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor, Listener, TabCompleter {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final String noHomeMessage;
    private final String homeTPMessage;
    private final String movingMessage;
    private final String noPermission;
    private final String delayMessage;
    BukkitTask movementTask = null;
    private final String badSender;
    private final String noInteger;

    public HomeCommand(JavaPlugin plugin, ConfigManager configManager) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.plugin = plugin;
        this.configManager = configManager;
        this.noHomeMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-home"));
        this.homeTPMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-tp"));
        this.movingMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("is-moving"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
        this.delayMessage = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("is-delay"));
        this.badSender = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-sender"));
        this.noInteger = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("home-no-integer"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                String subCommand = args[0];
                String subCommand2 = null;
                if (args.length > 1) {
                    subCommand2 = args[1];
                }

                if (subCommand.equalsIgnoreCase("setdescription")) {
                    SetDescriptionCommand setDescriptionCommand = new SetDescriptionCommand(configManager);
                    return setDescriptionCommand.onCommand(sender, command, label, args);
                } else if (subCommand.equalsIgnoreCase("delete")) {
                    DeleteHomeCommand deleteHomeCommand = new DeleteHomeCommand(configManager);
                    return deleteHomeCommand.onCommand(sender, command, label, args);
                } else {
                    if (subCommand2 == null) {
                        ProcessHomeCommand(player, args);
                    } else {
                        if (subCommand2.equalsIgnoreCase("setdescription")) {
                            SetDescriptionCommand setDescriptionCommand = new SetDescriptionCommand(configManager);
                            return setDescriptionCommand.onCommand(sender, command, label, args);
                        } else if (subCommand2.equalsIgnoreCase("delete")) {
                            DeleteHomeCommand deleteHomeCommand = new DeleteHomeCommand(configManager);
                            return deleteHomeCommand.onCommand(sender, command, label, args);
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                ProcessHomeCommand(player, args);
            }
            return true;
        } else {
            sender.sendMessage(badSender);
            return true;
        }
    }

    private void ProcessHomeCommand(Player player, String[] args) {
        if (player.hasPermission("myhome.home")) {
            FileConfiguration homesConfig = configManager.getHomesConfig();
            FileConfiguration mainConfig = configManager.getMainConfig();

            if (homesConfig.get("homes" + "." + player.getUniqueId().toString()) != null) {
                int homeNumber = 0;
                String isOk = "yes";
                if (args.length == 0 || args[0].equalsIgnoreCase("home")) {
                    homeNumber = mainConfig.getInt("default-tp-home");
                } else {
                    try {
                        homeNumber = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        isOk = "no";
                        player.sendMessage(noInteger.replace("{player}", player.getName()));
                    }
                }

                if (isOk.equals("yes")) {
                    int finalHomeNumber = homeNumber;

                    if (mainConfig.getBoolean("delay-require")) {
                        int delaySeconds = mainConfig.getInt("delay");
                        String delayStr = String.valueOf(delaySeconds);

                        player.sendMessage(delayMessage.replace("{player}", player.getName()).replace("{delay}", delayStr));

                        if (mainConfig.getBoolean("allow-movement")) {
                            int taskId = Bukkit.getScheduler().runTaskLater(configManager.getPlugin(), () -> {
                                if (player.isOnline() && !player.isDead()) {
                                    float yaw = player.getLocation().getYaw();
                                    float pitch = player.getLocation().getPitch();

                                    teleportHome(player, homesConfig, (int) yaw, (int) pitch, finalHomeNumber);
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
                                        float yaw = player.getLocation().getYaw();
                                        float pitch = player.getLocation().getPitch();

                                        teleportHome(player, homesConfig, (int) yaw, (int) pitch, finalHomeNumber);
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
                        float yaw = player.getLocation().getYaw();
                        float pitch = player.getLocation().getPitch();

                        teleportHome(player, homesConfig, (int) yaw, (int) pitch, finalHomeNumber);
                    }
                }
            } else {
                player.sendMessage(noHomeMessage.replace("{player}", player.getName()));
            }
        } else {
            player.sendMessage(noPermission.replace("{player}", player.getName()));
        }
    }

    private void teleportHome(Player player, FileConfiguration homesConfig, Integer yawDefault, Integer pitchDefault, Integer homeNumber) {
        FileConfiguration mainConfig = configManager.getMainConfig();

        double x = homesConfig.getDouble("homes" + "." + player.getUniqueId().toString() + "." + homeNumber + ".location.x");
        double y = homesConfig.getDouble("homes" + "." + player.getUniqueId().toString() + "." + homeNumber + ".location.y");
        double z = homesConfig.getDouble("homes" + "." + player.getUniqueId().toString() + "." + homeNumber + ".location.z");
        String worldName = homesConfig.getString("homes" + "." + player.getUniqueId().toString() + "." + homeNumber + ".location.world");

        if (mainConfig.getBoolean("save-look")) {
            float yaw = (float) configManager.getHomesConfig().getDouble("homes" + "." + player.getUniqueId().toString() + "." + homeNumber + ".location.yaw");
            float pitch = (float) configManager.getHomesConfig().getDouble("homes" + "." + player.getUniqueId().toString() + "." + homeNumber + ".location.pitch");

            player.teleport(new Location(player.getServer().getWorld(worldName), x, y, z, yaw, pitch));
        } else {
            float yaw = (float) yawDefault;
            float pitch = (float) pitchDefault;

            player.teleport(new Location(player.getServer().getWorld(worldName), x, y, z, yaw, pitch));
        }

        player.sendMessage(homeTPMessage.replace("{player}", player.getName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String enteredCommand = args[0].toLowerCase();

            List<String> subCommands = new ArrayList<>();

            subCommands.add("setdescription");
            subCommands.add("delete");

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(enteredCommand)) {
                    completions.add(subCommand);
                }
            }
        }

        completions.replaceAll(completion -> completion.replaceFirst("^myhome:", ""));

        completions.sort(String.CASE_INSENSITIVE_ORDER);
        return completions;
    }

}
