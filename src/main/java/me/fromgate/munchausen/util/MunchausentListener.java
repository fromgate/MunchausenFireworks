package me.fromgate.munchausen.util;

import me.fromgate.munchausen.Cfg;
import me.fromgate.munchausen.ItemUtil;
import me.fromgate.munchausen.Munchausen;
import me.fromgate.munchausen.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import static me.fromgate.munchausen.Util.fireworkAtLocation;


public class MunchausentListener implements Listener {


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRocketCraft(CraftItemEvent event) {
        if (!ItemUtil.compareItemStr(event.getRecipe().getResult(), Cfg.rocketItem)) return;
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBombCraft(CraftItemEvent event) {
        if (Cfg.bombItemStr.equalsIgnoreCase("TNT")) return;
        if (!ItemUtil.compareItemStr(event.getRecipe().getResult(), Cfg.bombItemStr)) return;
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
            event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRocketPowerUp(PrepareItemCraftEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (!ItemUtil.compareItemStr(result, Cfg.rocketItem)) return;
        int addPower = 0;
        for (ItemStack item : event.getInventory().getContents()) {
            if (item == null) continue;
            if (ItemUtil.compareItemStr(item, Cfg.rocketItem)) {
                result = item.clone();
                result.setAmount(1);
            } else if (item.getType() == Material.FIREWORK) {
                FireworkMeta fwm = (FireworkMeta) item.getItemMeta();
                String name = result.getItemMeta().hasDisplayName() ? result.getItemMeta().getDisplayName() : "";
                if (!name.isEmpty()) fwm.setDisplayName(name);
                result.setItemMeta(fwm);
                result.setAmount(1);
            }
            if (item.getType() == Material.SULPHUR) addPower++;
        }

        FireworkMeta fwm = (FireworkMeta) result.getItemMeta();
        if (fwm.getPower() + addPower <= Math.min(128, Cfg.maxPower)) {
            fwm.setPower(fwm.getPower() + addPower);
            result.setItemMeta(fwm);
        } else result = null;
        if ((!Util.hasItemInCraftInventory(event.getInventory(), Cfg.rocketItem)) && event.getInventory().contains(Material.SULPHUR))
            result = null;

        for (HumanEntity he : event.getViewers()) {
            if (!he.hasPermission("munchausen.fireworks.craft")) result = null;
        }
        event.getInventory().setResult(result);
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        UpdateChecker.updateMsg(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobFirework(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof LivingEntity)) return;
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand() == null) return;
        if (event.getRightClicked().getType() == EntityType.PLAYER) {
            if (!player.hasPermission("munchausen.firework.launchplayer")) return;
        } else {
            if (!player.hasPermission("munchausen.firework.launchmob")) return;
        }
        if (!Util.compareItemStr(player.getInventory().getItemInMainHand(), Cfg.rocketItem)) return;
        if (Cfg.removeRocketItem && (player.getGameMode() != GameMode.CREATIVE)) {
            if (player.getInventory().getItemInMainHand().getAmount() > 1)
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            else player.getInventory().setItemInMainHand(null);
        }
        fireworkAtLocation((LivingEntity) event.getRightClicked(), event.getRightClicked().getLocation(), player.getInventory().getItemInMainHand(), false);
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSetFirework(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (Util.compareItemStr(player.getInventory().getItemInMainHand(), Cfg.rocketItem)) {
            if (!player.hasPermission("munchausen.firework")) return;
            if (Cfg.requireSprint && (!player.isSprinting())) {
                Util.normalizeFireworkPower(player.getInventory().getItemInMainHand());
                return;
            }
            if (player.getInventory().getItemInMainHand() == null) return;
            event.setCancelled(true);
            if (Cfg.removeRocketItem && (player.getGameMode() != GameMode.CREATIVE)) {
                if (player.getInventory().getItemInMainHand().getAmount() > 1)
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                else player.getInventory().setItemInMainHand(null);
            }
            fireworkAtLocation(player, event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), player.getInventory().getItemInMainHand(), false);
        } else if (Util.compareItemStr(player.getInventory().getItemInMainHand(), Cfg.carrierItem)) {
            event.setCancelled(true);
            if (!player.hasPermission("munchausen.carrier")) return;
            if (Cfg.requireSprint && (!player.isSprinting())) {
                event.setCancelled(true);
                return;
            }
            if (!Util.burnFuel(player)) return;
            fireworkAtLocation(player, event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), player.getInventory().getItemInMainHand(), true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerFall(EntityDamageEvent event) {
        if (!event.getEntity().hasMetadata("mnch-dmg-modifier")) return;
        double modifier = event.getEntity().getMetadata("mnch-dmg-modifier").get(0).asDouble();
        event.getEntity().removeMetadata("mnch-dmg-modifier", Munchausen.getPlugin());
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        event.setDamage(event.getDamage() * modifier);
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onBomberMan(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!player.hasPermission("munchausen.bomberman")) return;
        if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
            return;
        if (!player.isInsideVehicle()) return;
        if (player.getVehicle().getType() != EntityType.FIREWORK) return;
        if (!ItemUtil.compareItemStr(player.getInventory().getItemInMainHand(), Cfg.bombItemStr))
            return;
        if (player.getGameMode() == GameMode.CREATIVE || ItemUtil.removeItemInHand(player, Cfg.bombItemStr)) {
            Vector vec = player.getLocation().toVector();
            Vector dir = player.getLocation().getDirection();
            vec = vec.add(dir.multiply(1.5));
            Location location = vec.toLocation(player.getWorld());
            TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
            if (Cfg.bombPlayerDir) dir = dir.normalize().multiply(1.8);
            else dir = new Vector(0, 0, 0);
            dir = dir.setY(-0.8);
            tnt.setVelocity(dir);
            tnt.getLocation().getWorld().playSound(tnt.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1.5f, 1.5f);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void checkIsPlayerMovingInWall(PlayerMoveEvent event) {
        if (!event.getPlayer().isInsideVehicle()) return;
        if (event.getPlayer().getVehicle().getType() != EntityType.FIREWORK) return;
        if (event.getTo().getBlock().getType() != Material.AIR
                || event.getTo().getBlock().getRelative(0, 1, 0).getType() != Material.AIR
                || event.getTo().getBlock().getRelative(0, 2, 0).getType() != Material.AIR) {
            Util.kickPlayer(event.getPlayer(), 1.5);
            Util.setFallDamageModifier(event.getPlayer());
            Util.setJumpEffects(event.getPlayer());
            Util.detonateAfterJump((Firework) event.getPlayer().getVehicle(), true);
        }
    }
}
