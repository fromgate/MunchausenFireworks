package me.fromgate.munchausen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Firework;

public class NMSLib {
	private static Munchausen plg(){
		return Munchausen.instance;
	}
	private static String [] tested_versions = {"v1_6_R2","v1_6_R3","v1_7_R1","v1_7_R2","v1_7_R3"};
	private static boolean disabled = true;
	private static boolean activated = false;
	private static String obcPrefix = "org.bukkit.craftbukkit.";
	private static String nmsPrefix = "net.minecraft.server.";
	private static String version = "";
	private static Class<?> CraftFirework;
	private static Method firework_getHandle;
	private static Class<?> EntityFireworks;
	private static Field expectedLifespan;
	private static Field ticksFlown;

	public static void init(){
		if (activated) return;
		String pkg = Bukkit.getServer().getClass().getPackage().getName();
		String [] v = pkg.split("\\.");
		if (v.length==4){
			version = v[3];
			obcPrefix = "org.bukkit.craftbukkit."+version+".";
			nmsPrefix = "net.minecraft.server."+version+".";;
		}
		isTestedInform();
		try {
			CraftFirework = Class.forName(obcPrefix+"entity.CraftFirework");
			firework_getHandle = CraftFirework.getMethod("getHandle"); //EntityFireworks
			EntityFireworks = Class.forName(nmsPrefix+"EntityFireworks");
			expectedLifespan = EntityFireworks.getField("expectedLifespan");
			ticksFlown = EntityFireworks.getDeclaredField("ticksFlown");
			ticksFlown.setAccessible(true);
			disabled = false;
		} catch (Exception e) {
			log("Failed to initialize NMSLib! Some features of plugin will be disabled!");
			log("Please download compatible version from: http://dev.bukkit.org/bukkit-plugins/playeffect/");

			e.printStackTrace();
			disabled = true;
		}
		activated = true;
	}

	private static void log(String string) {
		plg().u.log(string);
	}

	public static boolean isTestedVersion(){
		for (int i = 0; i< tested_versions.length;i++){
			if (tested_versions[i].equalsIgnoreCase(version)) return true;
		}
		return false;
	}

	public static String getVersion(){
		return version;
	}

	public static void isTestedInform(){
		if (isTestedVersion()) return;
		log("Warning! MunchausenFirewors was not tested with craftbukkit version "+version.replace("_", "."));
		log("Check updates at http://dev.bukkit.org/bukkit-plugins/munchausen/");
		log("or use this version at your own risk");
	}

	public static boolean isDisabled(){
		return disabled;
	}


	public static boolean setFireworkMaxPower (Firework fw, float seconds){
		if (disabled) return false;
		try{
			Object nms_firework = firework_getHandle.invoke(fw);
			expectedLifespan.set(nms_firework, (int)seconds*20);
			return true;
		}catch(Exception e){
			disabled = true;
		}
		return false;
	}

	public static boolean isTimeToUpdateFireworkPower (Firework fw){
		if (disabled) return false;
		try{
			Object nms_firework = firework_getHandle.invoke(fw);
			int expected = (Integer) expectedLifespan.get(nms_firework);
			int ticks = (Integer) ticksFlown.get(nms_firework);
			return ((expected-ticks)<=2);
		}catch(Exception e){
			disabled = true;
		}
		return false;
	}

	public static void resetFireworkPower (Firework fw){
		if (disabled) return;
		try{
			Object nms_firework = firework_getHandle.invoke(fw);
			ticksFlown.set(nms_firework, (int) 1);
		}catch(Exception e){
			disabled = true;
		}
	}
}
