package me.fromgate.munchausen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class Munchausen extends JavaPlugin {
    //Configuration
    String language = "english";
    boolean saveLanguage = false;
    boolean versionCheck = true;

    //Common
    int maxPower = 50;
    boolean requireSprint=true;
    boolean useCraft;
    boolean takeOffSpeed;
    
    //MunchausenFirework

    String rocketItem = Material.FIREWORK.name(); 
    boolean removeRocketItem = true;
    boolean randomFirework = false;
    float rocketTakeOffModifier = 0.8f;
    
    //CarrierFirework
    boolean useFuel = true;
    String carrierItem = "&cCarrier_Firework$FIREWORK";
    String carrierFuel = Material.SULPHUR.name();
    float carrierBaseTime = 1f;
    float carrierTakeOffModifier = 0.45f;
    boolean carrierExplodeOnReload = true;
    
    // JumpOut 
    double damagePercent = 0; //0 - remove damage, 100 - normal damage 
    List<String> effectOnJump = new ArrayList<String>();
    
    // Bombs
    String bombItemStr = "&4Air bomb$TNT";
    boolean bombPlayerDir = true; 
    
    
    static Munchausen instance;
    Util u;
    public static Munchausen getPlugin(){
    	return instance;
    }
    public static Util getUtil(){
    	return instance.u;
    }
    
    @Override
    public void onEnable(){
        reloadCfg();
        instance = this;
        u = new Util(this, saveLanguage, language);
        getCommand("munchausen").setExecutor(new Comander());
        ItemUtil.init(this);
        NMSLib.init();
        getServer().getPluginManager().registerEvents(u, this);
    }
    
    public void reloadCfg(){
        reloadConfig();
        language = getConfig().getString("general.language","english");
        getConfig().set("general.language",language);
        saveLanguage = getConfig().getBoolean("general.language-save",false);
        getConfig().set("general.language-save",saveLanguage);
        versionCheck = getConfig().getBoolean("general.check-updates",true);
        getConfig().set("general.check-updates",versionCheck);
        
        // CommonConfiguration
        requireSprint = getConfig().getBoolean("firework.sprint-to-fly",true);
        getConfig().set("firework.sprint-to-fly",requireSprint);
        useCraft = getConfig().getBoolean("firework.item-craft-enable",true);
        getConfig().set("firework.item-craft-enable",useCraft);
        takeOffSpeed = getConfig().getBoolean("firework.decrease-takeoff-speed",true);
        getConfig().set("firework.decrease-takeoff-speed",takeOffSpeed);

        // MunchausenFirework
        rocketItem = getConfig().getString("firework.munchausen.item","&6Munchausen_Firework$FIREWORK");
        if (ItemUtil.parseItemStack(rocketItem)==null) rocketItem = "&6Munchausen_Firework$FIREWORK";
        getConfig().set("firework.munchausen.item",rocketItem);
        maxPower = getConfig().getInt("firework.munchausen.maxPower",50);
        getConfig().set("firework.munchausen.maxPower",maxPower );
        rocketTakeOffModifier = (float) getConfig().getDouble("firework.munchausen.take-off-modifier",0.8f);
        getConfig().set("firework.munchausen.take-off-modifier",rocketTakeOffModifier);
        removeRocketItem = getConfig().getBoolean("firework.munchausen.item-remove",true);
        getConfig().set("firework.munchausen.item-remove",removeRocketItem);
        randomFirework = getConfig().getBoolean("firework.munchausen.randomize-firework",false);
        getConfig().set("firework.munchausen.randomize-firework",randomFirework);

        // CarrierFirework
        useFuel = getConfig().getBoolean("firework.carrier.need-fuel-to-fly",true);
        getConfig().set("firework.carrier.need-fuel-to-fly",useFuel);
        carrierItem = getConfig().getString("firework.carrier.item","&cCarrier_Firework$FIREWORK");
        if (ItemUtil.parseItemStack(carrierItem)==null) carrierItem = "&cCarrier_Firework$FIREWORK";
        getConfig().set("firework.carrier.item",carrierItem);
        carrierFuel = getConfig().getString("firework.carrier.fuel-item",Material.SULPHUR.name());
        if (ItemUtil.parseItemStack(carrierFuel)==null) carrierFuel = Material.SULPHUR.name();
        getConfig().set("firework.carrier.fuel-item",carrierFuel);
        carrierTakeOffModifier = (float) getConfig().getDouble("firework.carrier.take-off-modifier",0.45f);
        getConfig().set("firework.carrier.take-off-modifier",carrierTakeOffModifier);
        carrierBaseTime = (float) getConfig().getDouble("firework.carrier.base-time",1f);
        getConfig().set("firework.carrier.base-time",carrierBaseTime);
        carrierExplodeOnReload = getConfig().getBoolean("firework.carrier.explode-effect-on-reload",true);
        getConfig().set("firework.carrier.explode-effect-on-reload",carrierExplodeOnReload);
        
        damagePercent = getConfig().getDouble("fall-from-firework.damage-modifier",100);
        getConfig().set("fall-from-firework.damage-modifier",damagePercent);
        
        // Air bombs
        bombItemStr = getConfig().getString("air-bombs.item","&4Air bomb$TNT");
        getConfig().set("air-bombs.item",bombItemStr);
        bombPlayerDir = getConfig().getBoolean("air-bombs.shoot-mode",true);
        getConfig().set("air-bombs.shoot-mode",bombPlayerDir);
        
        if (getConfig().isSet("fall-from-firework.effects")) effectOnJump = getConfig().getStringList("fall-from-firework.effects");
        else {
        	effectOnJump = new ArrayList<String>();
        	effectOnJump.add("DAMAGE_RESISTANCE time:1s level:3");
        }
 
        getConfig().set("fall-from-firework.effects",effectOnJump);
        saveConfig();
    }



}