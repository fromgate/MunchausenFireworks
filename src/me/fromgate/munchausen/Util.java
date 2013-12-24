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
        this.initUpdateChecker("Munchausen Fireworks", "70681", "munchausen", plg.version_check);
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
    }
    
    
    private boolean hasItemInCraftInventory (Inventory inv, String itemStr){
        if (inv == null) return false;
        for (int i = 1; i<inv.getContents().length;i++){
            if (inv.getContents()[i]!= null&&inv.getContents()[i].getType() != Material.AIR) 
            if (ItemUtil.compareItemStr(inv.getContents()[i], itemStr)) return true;
        }
        return false;
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
        fireworkAtLocation ((LivingEntity) event.getRightClicked(), event.getRightClicked().getLocation(), p.getItemInHand());
    }


    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
    public void onSetFirework(PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player p = event.getPlayer();
        if (!compareItemStr(p.getItemInHand(), plg.rocketItem)) return;
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
        fireworkAtLocation (p, event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), p.getItemInHand());
    }

    public void kickPlayer(final LivingEntity p, final double mult){
        p.setVelocity(p.getEyeLocation().getDirection().multiply(mult));
    }


    public void changeFireworkDirection(final Firework f, final LivingEntity p){
        if (f.isDead()||(f.getPassenger() == null)){
            kickPlayer (p,1.5);
            detonateAfterJump (f);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plg, new Runnable(){
            @Override
            public void run() {
                Vector dir = p.getEyeLocation().getDirection().multiply(0.1).setY(0);
                f.setVelocity(f.getVelocity().normalize().add(dir).multiply(new Vector(1,plg.takeOffSpeed ? 0.8:1,1)));
                f.setTicksLived(1);
                changeFireworkDirection (f,p);
            }
        }, 1);
    }

    public void fireworkAtLocation (LivingEntity p, Location loc, ItemStack source){
        Firework f = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fm = null;
        if (!plg.randomFirework&&(source.getType() == Material.FIREWORK)){
            fm = (FireworkMeta) source.getItemMeta();
        } else {
            fm = (FireworkMeta) f.getFireworkMeta();
            fm.addEffect(FireworkEffect.builder().with(getRandomFireworkType()).withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255))).flicker(true).build());
            if (fm.getPower()<=1) fm.setPower(5); 
        }
        f.setFireworkMeta(fm);
        f.setPassenger(p);
        changeFireworkDirection (f,p);
    }

    public FireworkEffect.Type getRandomFireworkType(){
        return FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
    }


    public void detonateAfterJump(final Firework f){
        Bukkit.getScheduler().runTaskLater(plg, new Runnable(){
            @Override
            public void run() {
                if (!f.isDead()) {
                    f.detonate();
                }
            }
        }, (1+random.nextInt(3))*4);
    }
    
    private void normalizeFireworkPower(ItemStack item){
        if (item.getType()!=Material.FIREWORK) return;
        FireworkMeta fm = (FireworkMeta) item.getItemMeta();
        fm.setPower(Math.min(4, fm.getPower()));
        item.setItemMeta(fm);
    }


}
