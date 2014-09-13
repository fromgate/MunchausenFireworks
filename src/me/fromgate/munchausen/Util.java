package me.fromgate.munchausen;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class Util extends FGUtilCore implements Listener {
    Munchausen plg;
    public Util(Munchausen plg, boolean savelng, String lng) {
        super(plg, savelng, lng, "munchausen", "munchausen");
        this.plg = plg;
        this.initUpdateChecker("Munchausen Fireworks", "70681", "munchausen", plg.versionCheck);
        if (plg.useCraft) addRecipes();
    }

    public void addRecipes(){
        ItemStack newRocket = ItemUtil.parseItemStack(plg.rocketItem);
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
        
        ShapelessRecipe carrier = new ShapelessRecipe (ItemUtil.parseItemStack(plg.carrierItem));
        carrier.addIngredient(Material.FIREWORK);
        carrier.addIngredient(Material.LEASH);
        carrier.addIngredient(Material.FURNACE);
        Bukkit.addRecipe(carrier);
    }
    
    
    private boolean hasItemInCraftInventory (Inventory inv, String itemStr){
        if (inv == null) return false;
        for (int i = 1; i<inv.getContents().length;i++){
            if (inv.getContents()[i]!= null&&inv.getContents()[i].getType() != Material.AIR) 
            if (ItemUtil.compareItemStr(inv.getContents()[i], itemStr)) return true;
        }
        return false;
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void checkIsPlayerMovingInWall (PlayerMoveEvent event){
    	if (!event.getPlayer().isInsideVehicle()) return;
    	if (event.getPlayer().getVehicle().getType()!=EntityType.FIREWORK) return;
    	if (event.getTo().getBlock().getType() != Material.AIR
    			||event.getTo().getBlock().getRelative(0,1,0).getType() != Material.AIR
    			||event.getTo().getBlock().getRelative(0,2,0).getType() != Material.AIR){
            kickPlayer (event.getPlayer(),1.5);
            detonateAfterJump ((Firework) event.getPlayer().getVehicle(),true);
    	}
    }
    

    /*
     * To prevent item dupe. I cannot find other way to prevent dupe...
     */
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void onRocketCraft (CraftItemEvent event){
        if (!ItemUtil.compareItemStr(event.getRecipe().getResult(), plg.rocketItem)) return;
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) event.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void onRocketPowerUp (PrepareItemCraftEvent event){
        ItemStack result = event.getRecipe().getResult();
        if (!ItemUtil.compareItemStr(result, plg.rocketItem)) return;
        int addPower = 0;
        for (ItemStack item : event.getInventory().getContents()){
            if (item == null) continue;
            if (ItemUtil.compareItemStr(item, plg.rocketItem)) {
                result = item.clone();
                result.setAmount(1);
            }
            else if (item.getType() == Material.FIREWORK) {
                FireworkMeta fwm = (FireworkMeta) item.getItemMeta();
                String name = result.getItemMeta().hasDisplayName() ? result.getItemMeta().getDisplayName() : "";
                if (!name.isEmpty()) fwm.setDisplayName(name);
                result.setItemMeta(fwm);
                result.setAmount(1);
            }
            if (item.getType() == Material.SULPHUR) addPower++;
        }
        
        FireworkMeta fwm = (FireworkMeta) result.getItemMeta();
        if (fwm.getPower()+addPower<=Math.min(128, plg.maxPower)) {
            fwm.setPower(fwm.getPower()+addPower);
            result.setItemMeta(fwm);
        } else result = null;
        if ((!hasItemInCraftInventory(event.getInventory(), plg.rocketItem))&&event.getInventory().contains(Material.SULPHUR)) result = null;
        
        for (HumanEntity he : event.getViewers()){
            if (!((Player) he).hasPermission("munchausen.fireworks.craft")) result = null;
        }
        event.getInventory().setResult(result);
    }


    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void onJoin (PlayerJoinEvent event){
        this.updateMsg(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobFirework(PlayerInteractEntityEvent event){
        if (!(event.getRightClicked() instanceof LivingEntity)) return;
        Player p = event.getPlayer();
        if (p.getItemInHand() == null) return;
        if (event.getRightClicked().getType() == EntityType.PLAYER){
            if (!p.hasPermission("munchausen.firework.launchplayer")) return;
        } else {
            if (!p.hasPermission("munchausen.firework.launchmob")) return;
        }
        if (!compareItemStr(p.getItemInHand(), plg.rocketItem)) return;
        if (plg.removeRocketItem&&(p.getGameMode() != GameMode.CREATIVE)){
            if (p.getItemInHand().getAmount()>1) p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
            else p.setItemInHand(null);
        }
        fireworkAtLocation ((LivingEntity) event.getRightClicked(), event.getRightClicked().getLocation(), p.getItemInHand(),false);
    }


    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void onSetFirework(PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Player p = event.getPlayer();
        if (compareItemStr(p.getItemInHand(), plg.rocketItem)) {
            if (!p.hasPermission("munchausen.firework")) return;
            if (plg.requireSprint&&(!p.isSprinting())) {
                normalizeFireworkPower (p.getItemInHand());
                return;
            }
            if (p.getItemInHand() == null) return;
            event.setCancelled(true);
            if (plg.removeRocketItem&&(p.getGameMode() != GameMode.CREATIVE)){
                if (p.getItemInHand().getAmount()>1) p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
                else p.setItemInHand(null);
            }
            fireworkAtLocation (p, event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), p.getItemInHand(),false);            
        } else if (compareItemStr(p.getItemInHand(), plg.carrierItem)){
        	event.setCancelled(true);
            if (!p.hasPermission("munchausen.carrier")) return;
            if (plg.requireSprint&&(!p.isSprinting())) {
                event.setCancelled(true);
                return;
            }
            if(!burnFuel(p)) return;    
            fireworkAtLocation (p, event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), p.getItemInHand(),true);
        }
    }

    public void kickPlayer(final LivingEntity entity, final double mult){
        entity.setVelocity(entity.getEyeLocation().getDirection().multiply(mult));
    }

    private boolean burnFuel(Player player){
    	if (!plg.useFuel) return true;
    	if (player.getGameMode() == GameMode.CREATIVE) return true;
    	if (!ItemUtil.hasItemInInventory(player, plg.carrierFuel)) return false;
    	ItemUtil.removeItemInInventory(player, plg.carrierFuel);
    	return true;
    }
    
    private void burnCarrierFuel(Firework carrier, LivingEntity entity){
        if (entity.getType() != EntityType.PLAYER) return;
        Player player = (Player) entity;
        if (NMSLib.isDisabled()) return;
        if (!NMSLib.isTimeToUpdateFireworkPower(carrier)) return;
        if(!burnFuel(player)) return;
        NMSLib.resetFireworkPower(carrier); 
        if (plg.carrierExplodeOnReload) player.getWorld().createExplosion(player.getLocation(), 0.01f);
    }

    public void changeFireworkDirection(final Firework firework, final LivingEntity entity, final boolean carrier){
        if (carrier) burnCarrierFuel(firework,entity); 
        
        if (firework.isDead()||(firework.getPassenger() == null)){
            kickPlayer (entity,1.5);
            detonateAfterJump (firework,carrier);
            return;
        }
        
        
        Bukkit.getScheduler().runTaskLater(plg, new Runnable(){
            @Override
            public void run() {
                Vector dir = entity.getEyeLocation().getDirection().multiply(0.1).setY(0);
                float takeOffModifier = (carrier ? plg.carrierTakeOffModifier: plg.rocketTakeOffModifier);
                firework.setVelocity(firework.getVelocity().normalize().add(dir).multiply(new Vector(1,plg.takeOffSpeed ? takeOffModifier:1,1)));
                firework.setTicksLived(1);
                changeFireworkDirection (firework,entity,carrier);
            }
        }, 1);
    }

    public void fireworkAtLocation (LivingEntity p, Location loc, ItemStack source, boolean carrier){
        Firework f = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fm = null;
        if (!plg.randomFirework&&(source.getType() == Material.FIREWORK)){
            fm = (FireworkMeta) source.getItemMeta();
        } else if (!carrier){
            fm = (FireworkMeta) f.getFireworkMeta();
            fm.addEffect(FireworkEffect.builder().with(getRandomFireworkType()).withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255))).flicker(true).build());
            if (fm.getPower()<=1) fm.setPower(5); 
        }
        f.setFireworkMeta(fm);
        if (carrier) NMSLib.setFireworkMaxPower(f, plg.carrierBaseTime);
        f.setPassenger(p);
        changeFireworkDirection (f,p,carrier);
    }

    public FireworkEffect.Type getRandomFireworkType(){
        return FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
    }


    public void detonateAfterJump(final Firework f, boolean carrier){
        Bukkit.getScheduler().runTaskLater(plg, new Runnable(){
            @Override
            public void run() {
                if (!f.isDead()) {
                    f.detonate();
                }
            }
        }, (1+(carrier ? 0 : random.nextInt(3))*4));
    }
    
    private void normalizeFireworkPower(ItemStack item){
        if (item.getType()!=Material.FIREWORK) return;
        FireworkMeta fm = (FireworkMeta) item.getItemMeta();
        fm.setPower(Math.min(4, fm.getPower()));
        item.setItemMeta(fm);
    }


}
