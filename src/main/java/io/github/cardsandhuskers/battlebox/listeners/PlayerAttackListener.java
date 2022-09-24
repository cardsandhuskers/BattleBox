package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.handlers.PlayerDeathHandler;
import io.github.cardsandhuskers.teams.objects.Team;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.Potion;


import static io.github.cardsandhuskers.battlebox.BattleBox.handler;
import static io.github.cardsandhuskers.battlebox.BattleBox.winningTeamsList;


public class PlayerAttackListener implements Listener {
    public PlayerPointsAPI ppAPI;
    public PlayerAttackListener(PlayerPointsAPI ppAPI) {
        this.ppAPI = ppAPI;
    }

    /**
     * Determines the source and target of attack and gets the players
     * Add any conditional handling to the damage method
     * @param e
     */
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        Player attacker;
        Player attacked;

        if(e.getEntity().getType() == EntityType.PLAYER) {
            attacked = (Player) e.getEntity();
            //if attack is from arrow
            if(e.getDamager().getType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) e.getDamager();
                attacker = (Player) arrow.getShooter();
                damage(attacker, attacked, e);

            //if attack is from player
            } else if(e.getDamager().getType() == EntityType.PLAYER) {
                attacker = (Player) e.getDamager();
                damage(attacker, attacked, e);
            } else if(e.getDamager().getType() == EntityType.SPLASH_POTION) {
                ThrownPotion potion = (ThrownPotion) e.getDamager();
                attacker = (Player) potion.getShooter();
                System.out.println(attacker);
                damage(attacker, attacked, e);
            }else {
                e.setCancelled(true);
            }
        } else {
            System.out.println("ARROW SHOT");
            e.setCancelled(true);
        }
    }

    /**
     * Handles the conditions for pvp damage and determines whether to cancel it
     * @param attacker
     * @param attacked
     * @param e
     */
    public void damage(Player attacker, Player attacked, EntityDamageByEntityEvent e) {
        //handle error if someone's team is null
        if (!(handler.getPlayerTeam(attacker) == null || handler.getPlayerTeam(attacked) == null)) {
            if (handler.getPlayerTeam(attacker).equals(handler.getPlayerTeam(attacked))) {
                e.setCancelled(true);
            } else {
                //if attack is from an arena that is done, cancel the event
                for(Team t:winningTeamsList) {
                    if(handler.getPlayerTeam(attacker).equals(t) || handler.getPlayerTeam(attacked).equals(t)) {
                        e.setCancelled(true);
                    }
                }
                if (attacked.getHealth() - e.getDamage() <= 0) {
                    //attacked.sendMessage("You Died");
                    e.setCancelled(true);
                    PlayerDeathHandler deathHandler = new PlayerDeathHandler(attacked);
                    ppAPI.give(attacker.getUniqueId(), 20);



                    for(Player p: handler.getPlayerTeam(attacker).getOnlinePlayers()) {
                        if(p.equals(attacker)) {
                            p.sendMessage("[+" + 20 + " points] " + handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                        } else {
                            p.sendMessage(handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                        }

                    }
                    for(Player p: handler.getPlayerTeam(attacked).getOnlinePlayers()) {
                        p.sendMessage(handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());

                    }
                    attacker.playSound(attacker.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 2f);
                    attacker.sendTitle("Killed " + handler.getPlayerTeam(attacked).color + attacked.getName(), "", 2, 20, 2);
                }
            }
        }
    }
}
