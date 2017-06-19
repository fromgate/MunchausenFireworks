package me.fromgate.munchausen;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;


public class Cfg {
    //Configuration
    public static String language = "english";
    public static boolean saveLanguage = false;
    public static boolean versionCheck = true;

    //Common
    public static int maxPower = 50;
    public static boolean requireSprint = true;
    public static boolean useCraft;
    public static boolean takeOffSpeed;

    //MunchausenFirework

    public static String rocketItem = Material.FIREWORK.name();
    public static boolean removeRocketItem = true;
    public static boolean randomFirework = false;
    public static float rocketTakeOffModifier = 0.8f;

    //CarrierFirework
    public static boolean useFuel = true;
    public static String carrierItem = "&cCarrier_Firework$FIREWORK";
    public static String carrierFuel = Material.SULPHUR.name();
    public static float carrierBaseTime = 1f;
    public static float carrierTakeOffModifier = 0.45f;
    public static boolean carrierExplodeOnReload = true;

    // JumpOut
    public static double damagePercent = 0; //0 - remove damage, 100 - normal damage
    public static List<String> effectOnJump = new ArrayList<String>();

    // Bombs
    public static String bombItemStr = "&4Air bomb$TNT";
    public static boolean bombPlayerDir = true;

    private static FileConfiguration config;

    public static void reload() {
        Munchausen.getPlugin().reloadConfig();

        language = config.getString("general.language", "english");
        config.set("general.language", language);
        saveLanguage = config.getBoolean("general.language-save", false);
        config.set("general.language-save", saveLanguage);
        versionCheck = config.getBoolean("general.check-updates", true);
        config.set("general.check-updates", versionCheck);

        // CommonConfiguration
        requireSprint = config.getBoolean("firework.sprint-to-fly", true);
        config.set("firework.sprint-to-fly", requireSprint);
        useCraft = config.getBoolean("firework.item-craft-enable", true);
        config.set("firework.item-craft-enable", useCraft);
        takeOffSpeed = config.getBoolean("firework.decrease-takeoff-speed", true);
        config.set("firework.decrease-takeoff-speed", takeOffSpeed);

        // MunchausenFirework
        rocketItem = config.getString("firework.munchausen.item", "&6Munchausen_Firework$FIREWORK");
        if (ItemUtil.parseItemStack(rocketItem) == null) rocketItem = "&6Munchausen_Firework$FIREWORK";
        config.set("firework.munchausen.item", rocketItem);
        maxPower = config.getInt("firework.munchausen.maxPower", 50);
        config.set("firework.munchausen.maxPower", maxPower);
        rocketTakeOffModifier = (float) config.getDouble("firework.munchausen.take-off-modifier", 0.8f);
        config.set("firework.munchausen.take-off-modifier", rocketTakeOffModifier);
        removeRocketItem = config.getBoolean("firework.munchausen.item-remove", true);
        config.set("firework.munchausen.item-remove", removeRocketItem);
        randomFirework = config.getBoolean("firework.munchausen.randomize-firework", false);
        config.set("firework.munchausen.randomize-firework", randomFirework);

        // CarrierFirework
        useFuel = config.getBoolean("firework.carrier.need-fuel-to-fly", true);
        config.set("firework.carrier.need-fuel-to-fly", useFuel);
        carrierItem = config.getString("firework.carrier.item", "&cCarrier_Firework$FIREWORK");
        if (ItemUtil.parseItemStack(carrierItem) == null) carrierItem = "&cCarrier_Firework$FIREWORK";
        config.set("firework.carrier.item", carrierItem);
        carrierFuel = config.getString("firework.carrier.fuel-item", Material.SULPHUR.name());
        if (ItemUtil.parseItemStack(carrierFuel) == null) carrierFuel = Material.SULPHUR.name();
        config.set("firework.carrier.fuel-item", carrierFuel);
        carrierTakeOffModifier = (float) config.getDouble("firework.carrier.take-off-modifier", 0.45f);
        config.set("firework.carrier.take-off-modifier", carrierTakeOffModifier);
        carrierBaseTime = (float) config.getDouble("firework.carrier.base-time", 1f);
        config.set("firework.carrier.base-time", carrierBaseTime);
        carrierExplodeOnReload = config.getBoolean("firework.carrier.explode-effect-on-reload", true);
        config.set("firework.carrier.explode-effect-on-reload", carrierExplodeOnReload);

        damagePercent = config.getDouble("fall-from-firework.damage-modifier", 100);
        config.set("fall-from-firework.damage-modifier", damagePercent);

        // Air bombs
        bombItemStr = config.getString("air-bombs.item", "&4Air bomb$TNT");
        config.set("air-bombs.item", bombItemStr);
        bombPlayerDir = config.getBoolean("air-bombs.shoot-mode", true);
        config.set("air-bombs.shoot-mode", bombPlayerDir);

        if (config.isSet("fall-from-firework.effects")) {
            effectOnJump = config.getStringList("fall-from-firework.effects");
        } else {
            effectOnJump = new ArrayList<String>();
            effectOnJump.add("DAMAGE_RESISTANCE time:1s level:3");
        }

        config.set("fall-from-firework.effects", effectOnJump);
        Munchausen.getPlugin().saveConfig();
    }


    static {
        config = Munchausen.getPlugin().getConfig();
    }
}
