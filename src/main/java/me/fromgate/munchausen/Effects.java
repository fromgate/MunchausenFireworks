package me.fromgate.munchausen;

import me.fromgate.munchausen.util.Param;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Effects {
	
	public static void setEffects (LivingEntity entity, List<String> effectList){
		if (effectList.isEmpty()) return;
		if (entity == null) return;
		if (entity.isDead()) return;
		for (PotionEffect effect : parseEffects (effectList)){
			if (entity.hasPotionEffect(effect.getType())) entity.removePotionEffect(effect.getType());
			entity.addPotionEffect(effect);
		}
	}

	public static List<PotionEffect> parseEffects (List<String> effectList){
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		for (String effectStr : effectList){
			if (effectStr.isEmpty()) continue;
			Param param = new Param(effectStr, "effect");
			String typeStr = param.getParam("effect", "");

			PotionEffectType potionType = parsePotionEffect (typeStr);
			if (potionType==null) continue;
			int level = Math.max(param.getParam("level", 1)-1, 0);
			int duration = Util.safeLongToInt(Util.timeToTicks(Util.parseTime(param.getParam("time", "1s"))));
			PotionEffect effect = new PotionEffect (potionType, duration, level,false);
			effects.add(effect);
		}
		return effects;
	}
	
	
	public static PotionEffectType parsePotionEffect (String name) {
		PotionEffectType pef = null;
		try{
			pef = PotionEffectType.getByName(name);
		} catch(Exception ignore){
		}
		return pef;
	}

	
}
