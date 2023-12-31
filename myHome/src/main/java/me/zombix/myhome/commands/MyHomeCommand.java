package me.zombix.myhome.commands;

import me.zombix.myhome.Config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyHomeCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    public MyHomeCommand(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String enteredCommand = args[0].toLowerCase();

            List<String> subCommands = new ArrayList<>();

            if (sender.hasPermission("myhome.reload") || sender.isOp()) {
                subCommands.add("reload");
            }

            subCommands.add("home");
            subCommands.add("sethome");

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
