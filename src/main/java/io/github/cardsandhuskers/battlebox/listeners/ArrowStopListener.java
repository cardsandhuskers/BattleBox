package io.github.cardsandhuskers.battlebox.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ArrowStopListener implements Listener {

    @EventHandler
    public void onArrowStop(ProjectileHitEvent e) {
        if(e.getHitBlock() != null) {
            if(e.getEntityType() == EntityType.ARROW) {
                Entity entity = e.getEntity();
                entity.remove();
            }

        }
    }
}
