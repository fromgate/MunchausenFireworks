package me.fromgate.munchausen;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class Munchausen extends JavaPlugin {
    String language = "english";
    boolean savelng = false;
    boolean version_check = true;
    String rocketItem = Material.FIREWORK.name(); // скорее всего от этого откажусь
    boolean removeRocketItem = true;
    boolean requireSprint=true;
    boolean randomFirework = false;
    int maxPower = 50;
    Util u;
    boolean takeOffSpeed;
    boolean useCraft;
    
    
    @Override
    public void onEnable(){
        reloadCfg();
        u = new Util(this, savelng, language); 
        Bukkit.getServer().getPluginManager().registerEvents(u, this);
    }
    
    private void reloadCfg(){
        reloadConfig();
        language = getConfig().getString("general.language","english");
        //getConfig().set("general.language",language);
        savelng = getConfig().getBoolean("general.language-save",false);
        //getConfig().set("general.language-save",savelng);
        version_check = getConfig().getBoolean("general.check-updates",true);
        getConfig().set("general.check-updates",version_check);
        rocketItem = getConfig().getString("firework.item","&6Munchausen_Firework$FIREWORK");
        if (ItemUtil.parseItemStack(rocketItem)==null) rocketItem = "&6Munchausen_Firework$FIREWORK";
        getConfig().set("firework.item",rocketItem);
        useCraft = getConfig().getBoolean("firework.item-craft-enable",true);
        getConfig().set("firework.item-craft-enable",useCraft);
        removeRocketItem = getConfig().getBoolean("firework.item-remove",true);
        getConfig().set("firework.item-remove",removeRocketItem);
        randomFirework = getConfig().getBoolean("firework.randomize-firewok",false);
        getConfig().set("firework.randomize-firewok",randomFirework);
        requireSprint = getConfig().getBoolean("firework.sprint-to-fly",true);
        getConfig().set("firework.sprint-to-fly",requireSprint);
        takeOffSpeed = getConfig().getBoolean("firework.decrease-takeoff-speed",true);
        getConfig().set("firework.decrease-takeoff-speed",takeOffSpeed);
        saveConfig();
    }



}
