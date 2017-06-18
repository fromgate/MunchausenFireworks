package me.fromgate.munchausen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
			Map<String,String> params = ParamUtil.parseParams(effectStr, "effect");
			String typeStr = ParamUtil.getParam(params, "effect", "");
			PotionEffectType potionType = parsePotionEffect (typeStr);
			if (potionType==null) continue;
			int level = Math.max(ParamUtil.getParam(params, "level", 1)-1, 0);
			int duration = Munchausen.getUtil().safeLongToInt(Munchausen.getUtil().timeToTicks(Munchausen.getUtil().parseTime(ParamUtil.getParam(params, "time", "1s"))));
			PotionEffect effect = new PotionEffect (potionType, duration, level,false);
			effects.add(effect);
		}
		return effects;
	}
	
	
	public static PotionEffectType parsePotionEffect (String name) {
		PotionEffectType pef = null;
		try{
			pef = PotionEffectType.getByName(name);
		} catch(Exception e){
		}
		return pef;
	}

	
}
