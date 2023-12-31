package me.zombix.myhome.Config;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String enteredCommand = args[0].toLowerCase();

            List<String> subCommands = new ArrayList<>();

            subCommands.add("sethome");
            subCommands.add("home");

            if (sender.hasPermission("myhome.reload") || sender.isOp()) {
                subCommands.add("reload");
            }

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(enteredCommand)) {
                    completions.add(subCommand);
                }
            }
        }

        completions.sort(String.CASE_INSENSITIVE_ORDER);
        return completions;
    }

}
