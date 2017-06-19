package me.fromgate.munchausen.commands;

import me.fromgate.munchausen.message.M;
import org.bukkit.command.CommandSender;


@CmdDefine(command = "munchausen", subCommands = "help", description = M.CMD_HELP, permission = "munchausen.help",
        allowConsole = true, shortDescription = "&3/munchausen help")
public class CmdHelp extends Cmd {


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Commander.printHelp(sender,1);
        return true;
    }


}
