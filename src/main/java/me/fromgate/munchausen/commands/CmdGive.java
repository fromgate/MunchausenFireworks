package me.fromgate.munchausen.commands;

import me.fromgate.munchausen.Cfg;
import me.fromgate.munchausen.ItemUtil;
import me.fromgate.munchausen.message.M;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@CmdDefine(command = "munchausen", subCommands = "give", description = M.CMD_GIVE, permission = "munchausen.give",
        shortDescription = "&3/munchausen give")
public class CmdGive extends Cmd{


    @Override
    public boolean execute(Player player, String[] args) {
        String title = M.GIVE_INV_TITLE.getText();
        if (title.length() > 32) title = title.substring(0, 32);
        Inventory inventory = Bukkit.createInventory(null, 9, title.isEmpty() ? "Munchausen Fireworks" : title);

        if (player.hasPermission("munchausen.firework"))
            inventory.addItem(ItemUtil.parseItemStack(Cfg.rocketItem));
        if (player.hasPermission("munchausen.carrier")) {
            inventory.addItem(ItemUtil.parseItemStack(Cfg.carrierItem));
            if (Cfg.useFuel) {
                for (int i = 0; i < 64; i++) {
                    if (!inventory.addItem(ItemUtil.parseItemStack(Cfg.carrierFuel)).isEmpty()) {
                        break;
                    }
                }
            }
        }
        if (player.hasPermission("munchausen.bomberman")) {
            ItemStack bomb = ItemUtil.parseItemStack(Cfg.bombItemStr);
            assert bomb != null;
            bomb.setAmount(64);
            inventory.addItem(bomb);
        }
        player.openInventory(inventory);
        return true;
    }


}
