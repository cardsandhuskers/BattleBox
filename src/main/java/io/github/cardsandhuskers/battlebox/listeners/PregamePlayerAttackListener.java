package io.github.cardsandhuskers.battlebox.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PregamePlayerAttackListener implements Listener {

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        e.setCancelled(true);
    }
}
