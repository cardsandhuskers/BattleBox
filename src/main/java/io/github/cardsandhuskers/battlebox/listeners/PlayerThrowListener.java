package io.github.cardsandhuskers.battlebox.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.net.http.WebSocket;

public class PlayerThrowListener implements Listener {

    @EventHandler
    public void onPlayerThrow(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }
}
