package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.BattleBox;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class PlayerJoinListener implements Listener {
    Plugin plugin;

    public PlayerJoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.teleport(plugin.getConfig().getLocation("WorldSpawn"));
        Inventory inv = p.getInventory();
        inv.clear();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            p.setGameMode(GameMode.SPECTATOR);
        },20L);
    }


}
