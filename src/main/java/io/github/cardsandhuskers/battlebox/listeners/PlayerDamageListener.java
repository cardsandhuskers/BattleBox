package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.objects.StoredAttacker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;
import static io.github.cardsandhuskers.battlebox.commands.StartGameCommand.timerStatus;

public class PlayerDamageListener implements Listener {
    //Designed to listen for environment damage
    private final BattleBox plugin = (BattleBox) Bukkit.getPluginManager().getPlugin("BattleBox");
    public PlayerDamageListener() {
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        //System.out.println(timerStatus);
        if(!timerStatus.equals("Round ends in")) {
            e.setCancelled(true);
        }
    }
}
