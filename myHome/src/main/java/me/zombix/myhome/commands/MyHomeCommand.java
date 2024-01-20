package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import me.zombix.myhome.Config.Updates;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MyHomeCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final Updates updates;
    private final String noPermission;

    public MyHomeCommand(JavaPlugin plugin, ConfigManager configManager, Updates updates) {
        FileConfiguration messagesConfig = configManager.getMessagesConfig();

        this.plugin = plugin;
        this.configManager = configManager;
        this.updates = updates;
        this.noPermission = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("no-permission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("myhome.myhome")) {
            if (args.length == 0) {
                return false;
            }

            String subCommand = args[0];

            if (subCommand.equalsIgnoreCase("sethome")) {
                SetHomeCommand setHomeCommand = new SetHomeCommand(configManager);
                return setHomeCommand.onCommand(sender, command, label, args);
            } else if (subCommand.equalsIgnoreCase("home")) {
                HomeCommand homeCommand = new HomeCommand(plugin, configManager);
                return homeCommand.onCommand(sender, command, label, args);
            } else if (subCommand.equalsIgnoreCase("reload")) {
                ReloadCommand reloadCommand = new ReloadCommand(configManager);
                return reloadCommand.onCommand(sender, command, label, args);
            } else if (subCommand.equalsIgnoreCase("homes")) {
                HomesCommand homesCommand = new HomesCommand(configManager);
                return homesCommand.onCommand(sender, command, label, args);
            } else if (subCommand.equalsIgnoreCase("addpermission")) {
                AddPermissionCommand addPermissionCommand = new AddPermissionCommand(configManager);
                return addPermissionCommand.onCommand(sender, command, label, args);
            } else if (subCommand.equalsIgnoreCase("editpermission")) {
                EditPermissionCommand editPermissionCommand = new EditPermissionCommand(configManager);
                return editPermissionCommand.onCommand(sender, command, label, args);
            } else if (subCommand.equalsIgnoreCase("deletepermission")) {
                DeletePermissionCommand deletePermissionCommand = new DeletePermissionCommand(configManager);
                return deletePermissionCommand.onCommand(sender, command, label, args);
            } else if (subCommand.equalsIgnoreCase("update")) {
                UpdateCommand updateCommand = new UpdateCommand(configManager, updates, plugin);
                return updateCommand.onCommand(sender, command, label, args);
            } else {
                return false;
            }
        } else {
            sender.sendMessage(noPermission.replace("{player}", sender.getName()));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String enteredCommand = args[0].toLowerCase();

            List<String> subCommands = new ArrayList<>();

            if (sender.hasPermission("myhome.reload")) {
                subCommands.add("reload");
            }
            if (sender.hasPermission("myhome.managepermissions")) {
                subCommands.add("addpermission");
                subCommands.add("editpermission");
                subCommands.add("deletepermission");
            }
            if (sender.hasPermission("myhome.update")) {
                subCommands.add("update");
            }

            subCommands.add("home");
            subCommands.add("sethome");
            subCommands.add("homes");

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(enteredCommand)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String enteredCommand = args[1].toLowerCase();

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
