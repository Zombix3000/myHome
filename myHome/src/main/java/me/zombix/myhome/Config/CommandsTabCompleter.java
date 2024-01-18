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

            switch (command.getName().toLowerCase()) {
                case "myhome":
                    subCommands.add("sethome");
                    subCommands.add("home");
                    subCommands.add("setdescription");

                    if (sender.hasPermission("myhome.reload") || sender.isOp()) {
                        subCommands.add("reload");
                    }
                    if (sender.hasPermission("myhome.managepermissions")) {
                        subCommands.add("addpermission");
                        subCommands.add("editpermission");
                        subCommands.add("deletepermission");
                    }
                    if (sender.hasPermission("myhome.reload")) {
                        subCommands.add("update");
                    }

                    break;
                case "home":
                    subCommands.add("setdescription");
                    subCommands.add("delete");

                    break;
            }

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(enteredCommand)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String enteredCommand = args[1].toLowerCase();

            List<String> subCommands = new ArrayList<>();

            switch (args[0].toLowerCase()) {
                case "home":
                    subCommands.add("setdescription");
                    subCommands.add("delete");

                    break;
            }

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
