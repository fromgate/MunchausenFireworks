package me.fromgate.munchausen.commands;


import me.fromgate.munchausen.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Commander implements CommandExecutor {
    private static List<Cmd> commands = new ArrayList<>();
    private static JavaPlugin plugin;
    private static Commander commander;

    public static void init(JavaPlugin plg) {
        plugin = plg;
        commander = new Commander();
        addNewCommand(new CmdHelp());
        addNewCommand(new CmdGive());
        addNewCommand(new CmdReload());
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static boolean addNewCommand(Cmd cmd) {
        if (cmd.getCommand() == null) return false;
        if (cmd.getCommand().isEmpty()) return false;
        plugin.getCommand(cmd.getCommand()).setExecutor(commander);
        commands.add(cmd);
        return true;
    }

    public static boolean isPluginYml(String cmdStr) {
        return plugin.getDescription().getCommands().containsKey(cmdStr);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {
        for (Cmd cmd : commands) {
            if (!cmd.getCommand().equalsIgnoreCase(command.getLabel())) continue;
            if (cmd.executeCommand(sender, args)) return true;
        }
        return false;
    }

    public static void printHelp(CommandSender sender, int page) {
        List<String> helpList = new ArrayList<>();
        for (Cmd cmd : commands) {
            helpList.add(cmd.getFullDescription());
        }
        M.printPage(sender, helpList, M.HELP_TITLE,1,10000);
    }


}
