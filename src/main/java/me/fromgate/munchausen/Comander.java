package me.fromgate.munchausen;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class Comander implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		String subCmd = "help";
		if (args.length>0) subCmd=args[0];
		if (!Munchausen.getUtil().checkCmdPerm(sender, subCmd)) return Munchausen.getUtil().returnMSG (true, sender, "cmd_cmdpermerr",'c');
		if (subCmd.equalsIgnoreCase("help")){
			Munchausen.instance.u.PrintHlpList(sender, 1, 100);
		} else if (subCmd.equalsIgnoreCase("give")){
			Player player = (Player) sender;
			String title = Munchausen.getUtil().getMSG("msg_inventorytitle",'5');
			if (title.length()>32) title = title.substring(0, 32);
			Inventory inventory = Bukkit.createInventory(null, 9, title.isEmpty() ? "Munchausen Fireworks" : title);
			if (player.hasPermission("munchausen.firework")) inventory.addItem(ItemUtil.parseItemStack(Munchausen.getPlugin().rocketItem));
			if (player.hasPermission("munchausen.carrier")) {
				inventory.addItem(ItemUtil.parseItemStack(Munchausen.getPlugin().carrierItem));
				if (Munchausen.getPlugin().useFuel)
					for (int i = 0; i<64; i++)
						if (!inventory.addItem(ItemUtil.parseItemStack(Munchausen.getPlugin().carrierFuel)).isEmpty()) break;	
			}
			if (player.hasPermission("munchausen.bomberman")){
				ItemStack bomb = ItemUtil.parseItemStack(Munchausen.getPlugin().bombItemStr);
				bomb.setAmount(64);
				inventory.addItem(bomb);
			}
			player.openInventory(inventory);
		} else if (subCmd.equalsIgnoreCase("reload")){
			Munchausen.getPlugin().reloadCfg();
			Munchausen.getUtil().printMSG(sender, "msg_reloaded");
		} 
		return true;
	}
}
