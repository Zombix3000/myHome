package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;

public class HomesCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final String noHomes;
    private final String headerHomesList;
    //private final String headerHomesListAnotherPlayer;
    private final String badSender;
    private final String noPermission;

    public HomesCommand(ConfigManager configManager) {
        this.configManager = configManager;

        FileConfiguration messagesConfig = configManager.getMessagesConfig();
        this.noHomes = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-home"));
        this.headerHomesList = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("header-homes-list"));
        this.badSender = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("bad-sender"));
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
        //this.headerHomesListAnotherPlayer = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("header-homes-list-another-player"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("myhome.homes")) {
                FileConfiguration homesConfig = configManager.getHomesConfig();
                String playerUUID = player.getUniqueId().toString();

                if (homesConfig.contains("homes" + "." + playerUUID)) {
                    ConfigurationSection playerHomes = homesConfig.getConfigurationSection("homes" + "." + playerUUID);
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
                            String description = homesConfig.getString("homes" + "." + playerUUID + "." + homeNumber + ".description", "");
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

    /*@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                String subCommand = args[0];
                String subCommand2 = null;
                if (args.length > 1) {
                    subCommand2 = args[1];
                }

                if (subCommand.equalsIgnoreCase("homes") && subCommand2 == null) {
                    ProcessHomesCommandDefault(player);
                } else if (args.length > 1 && !subCommand2.equalsIgnoreCase("homes")) {
                    ProcessHomesCommandAdmin(player, args[1]);
                } else if (args.length > 0 && !subCommand.equalsIgnoreCase("homes")) {
                    ProcessHomesCommandAdmin(player, args[0]);
                }
            } else {
                ProcessHomesCommandDefault(player);
            }
        } else {
            sender.sendMessage(badSender);
        }
        return true;
    }

    private void ProcessHomesCommandDefault(Player player) {
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
        }
    }

    private void ProcessHomesCommandAdmin(Player player, String args) {
        if (player.hasPermission("myhome.admin")) {
            FileConfiguration homesConfig = configManager.getHomesConfig();
            String playerName = args;
            UUID uuid = getUUID(playerName);
            String playerUUID = uuid.toString();

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
                    homesList.append(headerHomesListAnotherPlayer.replace("{player}", player.getName()));

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
        }
    }

    private static UUID getUUID(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                String idString = jsonObject.get("id").getAsString();
                return UUID.fromString(idString.substring(0, 8) + "-" + idString.substring(8, 12) + "-" + idString.substring(12, 16) + "-" + idString.substring(16, 20) + "-" + idString.substring(20));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
