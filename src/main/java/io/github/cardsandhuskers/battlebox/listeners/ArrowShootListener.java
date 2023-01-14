package io.github.cardsandhuskers.battlebox.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArrowShootListener implements Listener {

    @EventHandler
    public void onArrowShoot(EntityShootBowEvent e) {
        Player p = (Player) e.getEntity();
        //Inventory inv = p.getInventory();

        //ItemStack arrow = new ItemStack(Material.ARROW, 12);
        //inv.setItem(17, arrow);
    }
}
