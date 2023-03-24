package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.BattleBox;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;

public class PlayerDamageListener implements Listener {
    //Designed to listen for environment damage
    private final BattleBox plugin = (BattleBox) Bukkit.getPluginManager().getPlugin("BattleBox");
    public PlayerDamageListener() {
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(gameState != GameState.ROUND_ACTIVE) {
            e.setCancelled(true);
        }
    }
}
