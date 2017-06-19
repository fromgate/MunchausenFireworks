package me.fromgate.munchausen;

import me.fromgate.munchausen.commands.Commander;
import me.fromgate.munchausen.message.BukkitMessenger;
import me.fromgate.munchausen.message.M;
import me.fromgate.munchausen.util.MunchausentListener;
import me.fromgate.munchausen.util.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public class Munchausen extends JavaPlugin {

    
    
    private static Munchausen instance;
    public static Munchausen getPlugin(){
    	return instance;
    }

    @Override
    public void onEnable(){
        instance = this;
        Cfg.reload();
        M.init("MunchausenFireworks", new BukkitMessenger(this), Cfg.language, false, Cfg.saveLanguage);
        Util.init();
        instance = this;
        // u = new Util(this, saveLanguage, language);
        Commander.init(this);
        ItemUtil.init(this);
        NMSLib.init();
        getServer().getPluginManager().registerEvents(new MunchausentListener(), this);
        UpdateChecker.init(this, "MunchausenFireworks", "70681","munchausen", Cfg.versionCheck);
    }
    



}
