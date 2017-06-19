package me.fromgate.munchausen;

import me.fromgate.munchausen.message.M;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.regex.Pattern;

public class Util {

    private static Random random;

    public static void init() {
        random = new Random();
		if (Cfg.useCraft) addRecipes();
	}

	@SuppressWarnings("deprecation")
	public static void addRecipes(){
		ItemStack newRocket = ItemUtil.parseItemStack(Cfg.rocketItem);
		for (int i = 1; i<=8;i++){
			ShapelessRecipe pwrrecipe = new ShapelessRecipe (newRocket);
			pwrrecipe.addIngredient(1, Material.FIREWORK);
			for (int j=0;j<i;j++) pwrrecipe.addIngredient(1, Material.SULPHUR);
			Bukkit.addRecipe(pwrrecipe);    
		}
		ShapelessRecipe rocket = new ShapelessRecipe (newRocket);
		rocket.addIngredient(Material.FIREWORK);
		rocket.addIngredient(Material.LEASH);
		Bukkit.addRecipe(rocket);

		ShapelessRecipe carrier = new ShapelessRecipe (ItemUtil.parseItemStack(Cfg.carrierItem));
		carrier.addIngredient(Material.FIREWORK);
		carrier.addIngredient(Material.LEASH);
		carrier.addIngredient(Material.FURNACE);
		Bukkit.addRecipe(carrier);
		if (!Cfg.bombItemStr.equalsIgnoreCase("TNT")){
			ShapelessRecipe bomb = new ShapelessRecipe (ItemUtil.parseItemStack(Cfg.bombItemStr));
			bomb.addIngredient(Material.TNT);
			bomb.addIngredient(Material.FLINT_AND_STEEL);
			bomb.addIngredient(Material.STONE_BUTTON);
			Bukkit.addRecipe(bomb);
		}
	}


	public static boolean hasItemInCraftInventory(Inventory inv, String itemStr){
		if (inv == null) return false;
		for (int i = 1; i<inv.getContents().length;i++){
			if (inv.getContents()[i]!= null&&inv.getContents()[i].getType() != Material.AIR) 
				if (ItemUtil.compareItemStr(inv.getContents()[i], itemStr)) return true;
		}
		return false;
	}

	 public static void kickPlayer(final LivingEntity entity, final double mult){
		 entity.setVelocity(entity.getEyeLocation().getDirection().multiply(mult));
	 }

	 public static boolean burnFuel(Player player){
		 if (!Cfg.useFuel) return true;
		 if (player.getGameMode() == GameMode.CREATIVE) return true;
		 if (!ItemUtil.hasItemInInventory(player, Cfg.carrierFuel)) return false;
		 ItemUtil.removeItemInInventory(player, Cfg.carrierFuel);
		 return true;
	 }

	 private static void burnCarrierFuel(Firework carrier, LivingEntity entity){
		 if (entity.getType() != EntityType.PLAYER) return;
		 Player player = (Player) entity;
		 if (NMSLib.isDisabled()) return;
		 if (!NMSLib.isTimeToUpdateFireworkPower(carrier)) return;
		 if(!burnFuel(player)) return;
		 NMSLib.resetFireworkPower(carrier);
		 if (Cfg.carrierExplodeOnReload) player.getWorld().createExplosion(player.getLocation(), 0.01f);
	 }

	 public static void changeFireworkDirection(final Firework firework, final LivingEntity entity, final boolean carrier){
		 if (carrier) burnCarrierFuel(firework,entity); 

		 if (firework.isDead()||firework.getPassengers() == null || firework.getPassengers().isEmpty()){
			 kickPlayer (entity,1.5);
			 setJumpEffects (entity);
			 setFallDamageModifier (entity);
			 detonateAfterJump (firework,carrier);
			 return;
		 }

		 Bukkit.getScheduler().runTaskLater(Munchausen.getPlugin(), () -> {
             Vector dir = entity.getEyeLocation().getDirection().multiply(0.1).setY(0);
             float takeOffModifier = (carrier ? Cfg.carrierTakeOffModifier: Cfg.rocketTakeOffModifier);
             firework.setVelocity(firework.getVelocity().normalize().add(dir).multiply(new Vector(1,Cfg.takeOffSpeed ? takeOffModifier:1,1)));
             firework.setTicksLived(1);
             changeFireworkDirection (firework,entity,carrier);
         }, 1);
	 }

	 public static void fireworkAtLocation(LivingEntity entity, Location loc, ItemStack source, boolean carrier){
		 Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		 FireworkMeta fm = null;
		 if (!Cfg.randomFirework&&(source.getType() == Material.FIREWORK)){
			 fm = (FireworkMeta) source.getItemMeta();
		 } else if (!carrier){
			 fm = (FireworkMeta) firework.getFireworkMeta();
			 fm.addEffect(FireworkEffect.builder().with(getRandomFireworkType()).withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255))).flicker(true).build());
			 if (fm.getPower()<=1) fm.setPower(5); 
		 }
		 firework.setFireworkMeta(fm);
		 if (carrier) NMSLib.setFireworkMaxPower(firework, Cfg.carrierBaseTime);
		 firework.addPassenger(entity);
		 changeFireworkDirection (firework,entity,carrier);
	 }

	 public static FireworkEffect.Type getRandomFireworkType(){
         return FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
	 }


	 public static void detonateAfterJump(final Firework f, boolean carrier){
		 Bukkit.getScheduler().runTaskLater(Munchausen.getPlugin(), new Runnable(){
			 @Override
			 public void run() {
				 if (!f.isDead()) {
					 f.detonate();
				 }
			 }
		 }, (1+(carrier ? 0 : random.nextInt(3))*4));
	 }

	 public static void normalizeFireworkPower(ItemStack item){
		 if (item.getType()!=Material.FIREWORK) return;
		 FireworkMeta fm = (FireworkMeta) item.getItemMeta();
		 fm.setPower(Math.min(4, fm.getPower()));
		 item.setItemMeta(fm);
	 }

	 public static void setJumpEffects(LivingEntity entity){
		 if (entity instanceof Player && ((Player)entity).getGameMode() == GameMode.CREATIVE) return;
		 Effects.setEffects(entity, Cfg.effectOnJump);
	 }

	 public static void setFallDamageModifier(LivingEntity entity){
		 if (Cfg.damagePercent == 100) return;
		 if (entity == null) return;
		 if (entity.isDead()) return;
		 double modifier = Cfg.damagePercent / 100;
		 entity.setMetadata("mnch-dmg-modifier", new FixedMetadataValue (Munchausen.getPlugin(), modifier));
	 }


    @SuppressWarnings("deprecation")
    public static boolean compareItemStr(ItemStack item, String str) {
        String itemstr = str;
        String name = "";
        if (itemstr.contains("$")) {
            name = str.substring(0, itemstr.indexOf("$"));
            name = ChatColor.translateAlternateColorCodes('&', name.replace("_", " "));
            itemstr = str.substring(name.length() + 1);
        }
        if (itemstr.isEmpty()) return false;
        if (!name.isEmpty()) {
            String iname = item.hasItemMeta() ? item.getItemMeta().getDisplayName() : "";
            if (!name.equals(iname)) return false;
        }
        return compareItemStrIgnoreName(item.getTypeId(), item.getDurability(), item.getAmount(), itemstr); // ;compareItemStr(item, itemstr);
    }


    @SuppressWarnings("deprecation")
    public static boolean compareItemStrIgnoreName(int item_id, int item_data, int item_amount, String itemstr) {
        if (!itemstr.isEmpty()) {
            int id = -1;
            int amount = 1;
            int data = -1;
            String[] si = itemstr.split("\\*");
            if (si.length > 0) {
                if ((si.length == 2) && si[1].matches("[1-9]+[0-9]*")) amount = Integer.parseInt(si[1]);
                String ti[] = si[0].split(":");
                if (ti.length > 0) {
                    if (ti[0].matches("[0-9]*")) id = Integer.parseInt(ti[0]);
                    else {
                        try {
                            id = Material.getMaterial(ti[0]).getId();
                        } catch (Exception e) {
                            M.logOnce("unknownitem" + ti[0], "Unknown item: " + ti[0]);
                        }
                    }
                    if ((ti.length == 2) && (ti[1]).matches("[0-9]*")) data = Integer.parseInt(ti[1]);
                    return ((item_id == id) && ((data < 0) || (item_data == data)) && (item_amount >= amount));
                }
            }
        }
        return false;
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        if (l > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) l;
    }

    public static Long timeToTicks(Long time) {
        //1000 ms = 20 ticks
        return Math.max(1, (time / 50));
    }

    private static Pattern INT = Pattern.compile("\\d+");


    private static boolean isInteger (String strInt) {
        return INT.matcher(strInt).matches();
    }

    public static Long parseTime(String time) {
        int hh = 0; // часы
        int mm = 0; // минуты
        int ss = 0; // секунды
        int tt = 0; // тики
        int ms = 0; // миллисекунды
        if (isInteger(time)) {
            ss = Integer.parseInt(time);
        } else if (time.matches("^[0-5][0-9]:[0-5][0-9]$")) {
            String[] ln = time.split(":");
            if (isInteger(ln[0])) mm = Integer.parseInt(ln[0]);
            if (isInteger(ln[1])) ss = Integer.parseInt(ln[1]);
        } else if (time.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
            String[] ln = time.split(":");
            if (isInteger(ln[0])) hh = Integer.parseInt(ln[0]);
            if (isInteger(ln[1])) mm = Integer.parseInt(ln[1]);
            if (isInteger(ln[2])) ss = Integer.parseInt(ln[2]);
        } else if (time.matches("^\\d+ms")) {
            ms = Integer.parseInt(time.replace("ms", ""));
        } else if (time.matches("^\\d+h")) {
            hh = Integer.parseInt(time.replace("h", ""));
        } else if (time.matches("^\\d+m$")) {
            mm = Integer.parseInt(time.replace("m", ""));
        } else if (time.matches("^\\d+s$")) {
            ss = Integer.parseInt(time.replace("s", ""));
        } else if (time.matches("^\\d+t$")) {
            tt = Integer.parseInt(time.replace("t", ""));
        }
        return (hh * 3600000L) + (mm * 60000L) + (ss * 1000L) + (tt * 50L) + ms;
    }

}
