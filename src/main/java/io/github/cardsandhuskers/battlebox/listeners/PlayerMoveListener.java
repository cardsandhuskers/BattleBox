package io.github.cardsandhuskers.battlebox.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(p.getLocation().getY() < 0) p.teleport(new Location(p.getWorld(), p.getLocation().getX(), 40, p.getLocation().getZ()));
    }
}
