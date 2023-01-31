package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.handlers.PlayerDeathHandler;
import io.github.cardsandhuskers.battlebox.objects.StoredAttacker;
import org.black_ixx.playerpoints.PlayerPointsAPI;
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
    private PlayerPointsAPI ppAPI;
    private BattleBox plugin = (BattleBox) Bukkit.getPluginManager().getPlugin("BattleBox");
    public PlayerDamageListener(PlayerPointsAPI ppAPI) {
        this.ppAPI = ppAPI;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(!timerStatus.equals("Round ends in")) {
            e.setCancelled(true);
            return;
        }
        if(e.getEntity() instanceof Player p) {
            if(e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.LAVA || e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if(p.getHealth() - e.getDamage() <= 0) {
                    for(StoredAttacker s:storedAttackers) {
                        if(s.getAttacked().equals(p)) {
                            Player attacker = s.getAttacker();
                            int numPoints = plugin.getConfig().getInt("killPoints");

                            //ppAPI.give(attacker.getUniqueId(), (int)(numPoints * multiplier));
                            handler.getPlayerTeam(attacker).addTempPoints(attacker, (numPoints * multiplier));
                            
                            for(Player player: handler.getPlayerTeam(attacker).getOnlinePlayers()) {
                                if(player.equals(attacker)) {
                                    player.sendMessage("[+" + numPoints * multiplier + " points] " + handler.getPlayerTeam(p).color + p.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                                } else {
                                    player.sendMessage(handler.getPlayerTeam(p).color + p.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                                }

                            }
                            for(Player player: handler.getPlayerTeam(p).getOnlinePlayers()) {
                                player.sendMessage(handler.getPlayerTeam(p).color + p.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());

                            }
                            attacker.playSound(attacker.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 2f);
                            attacker.sendTitle("Killed " + handler.getPlayerTeam(p).color + p.getName(), "", 2, 16, 2);
                            
                            
                            
                        }
                    }
                    //p.sendMessage("Dead");
                    e.setCancelled(true);
                    PlayerDeathHandler deathHandler = new PlayerDeathHandler(p);
                }
            }
        }
    }
}
