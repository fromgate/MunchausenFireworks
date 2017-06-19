package me.fromgate.munchausen.commands;


import me.fromgate.munchausen.Cfg;
import me.fromgate.munchausen.message.M;
import org.bukkit.command.CommandSender;

@CmdDefine(command = "munchausen", subCommands = "reload", description = M.CMD_HELP, permission = "munchausen.config",
        allowConsole = true, shortDescription = "&3/munchausen reload")
public class CmdReload extends Cmd {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Cfg.reload();
        M.MSG_RELOADED.print(sender);
        return true;
    }
}
